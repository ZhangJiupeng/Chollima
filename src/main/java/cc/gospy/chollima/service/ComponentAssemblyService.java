package cc.gospy.chollima.service;

import cc.gospy.chollima.dao.*;
import cc.gospy.chollima.entity.bean.Assembly;
import cc.gospy.chollima.entity.bean.Component;
import cc.gospy.chollima.entity.bean.Spider;
import cc.gospy.chollima.entity.bean.TaskGroup;
import cc.gospy.chollima.entity.component.Component.Type;
import cc.gospy.chollima.entity.component.ComponentFactory;
import cc.gospy.chollima.repo.Spiders;
import cc.gospy.core.Gospy;
import cc.gospy.core.fetcher.FetchException;
import cc.gospy.core.fetcher.Fetcher;
import cc.gospy.core.fetcher.FetcherNotFoundException;
import cc.gospy.core.pipeline.PipeException;
import cc.gospy.core.pipeline.Pipeline;
import cc.gospy.core.pipeline.PipelineNotFoundException;
import cc.gospy.core.processor.ProcessException;
import cc.gospy.core.processor.Processor;
import cc.gospy.core.processor.ProcessorNotFoundException;
import cc.gospy.core.scheduler.impl.VerifiableScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ComponentAssemblyService {
    private static final Logger logger = LoggerFactory.getLogger(Gospy.class);

    @Autowired
    private ComponentDao componentDao;
    @Autowired
    private SpiderDao spiderDao;
    @Autowired
    private AssemblyDao assemblyDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskGroupDao taskGroupDao;

    public Component addComponent(Component component) {
        return componentDao.addComponent(component);
    }

    public boolean deleteComponent(Component component) {
        return componentDao.deleteComponent(component);
    }

    public List<Component> listComponents(Type type) {
        return componentDao.listComponents(type);
    }

    public List<Component> listComponents() {
        return componentDao.listComponents();
    }

    public Spider addSpider(Spider spider, List<Component> components) {
        Spider s = spiderDao.addSpider(spider);
        if (s == null) {
            throw new RuntimeException("add spider failure");
        }
        components.forEach(e -> {
            componentDao.increaseReference(e);
            Assembly assembly = new Assembly();
            assembly.setSpiderId(s.getId());
            assembly.setComponentId(e.getId());
            assemblyDao.addAssemblyRelationship(assembly);
        });
        return s;
    }

    public void deleteSpider(Spider spider) {
        Spider s = spiderDao.getSpiderById(spider.getId());
        if (s.getGroupId() != 0) {
            throw new RuntimeException("spider in use now");
        }
        assemblyDao.getAssemblyRelationshipsBySpiderId(spider.getId()).forEach(assembly -> {
            assemblyDao.deleteAssemblyRelationship(assembly);
            Component component = new Component();
            component.setId(assembly.getComponentId());
            componentDao.decreaseReference(component);
        });
        spiderDao.deleteSpider(spider);
    }

    public List<Spider> listSpiders() {
        return spiderDao.listSpiders();
    }

    public void initSpider(Spider spider, int groupId) {
        int spiderId = spider.getId();
        spider = spiderDao.getSpiderById(spiderId);
        if (spider.getGroupId() != 0) {
            throw new RuntimeException("[" + spider.getName() + "] was assigned to ["
                    + taskGroupDao.getTaskGroupById(spider.getGroupId()).getName() + "]");
        }
        spider.setGroupId(groupId);
        spiderDao.updateSpiderById(spider);

        TaskGroup taskGroup = taskGroupDao.getTaskGroupById(groupId);
        taskGroup.setStatus(1);
        taskGroup.setLastModifyTime(System.currentTimeMillis());
        taskGroupDao.updateTaskGroupById(taskGroup);

        Gospy.Builder builder = Gospy.custom()
                .setIdentifier(spider.getName())
                .setScheduler(VerifiableScheduler.custom()
                        .setExitThresholdInSeconds(10)
                        .setExitCallback(() -> {
                            logger.info("Receive terminate signal, exit in 3 seconds");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Spiders.gospyMap.get(groupId).stop();
                            Spiders.gospyMap.remove(groupId);
                            taskGroup.setStatus(1);
                            taskGroup.setLastModifyTime(System.currentTimeMillis());
                            taskGroupDao.updateTaskGroupById(taskGroup);
                            logger.info("Task status updated");
                        })
                        .build());
        assemblyDao.getAssemblyRelationshipsBySpiderId(spiderId).forEach(assembly -> {
            Component component = componentDao.getComponentById(assembly.getComponentId());
            try {
                if (component.getType() == Type.Fetcher.getValue()) {
                    cc.gospy.chollima.entity.component.Component<Fetcher> fetcher = ComponentFactory.getFetchComponent(component.getName(), component.getClazz(), component.getConfig());
                    builder.addFetcher(fetcher.getCore());
                    logger.info("Add Fetcher: {}", component.getName());
                } else if (component.getType() == Type.Processor.getValue()) {
                    cc.gospy.chollima.entity.component.Component<Processor> processor = ComponentFactory.getProcessComponent(component.getName(), component.getClazz(), component.getConfig());
                    builder.addProcessor(processor.getCore());
                    logger.info("Add Processor: {}", component.getName());
                } else if (component.getType() == Type.Pipeline.getValue()) {
                    cc.gospy.chollima.entity.component.Component<Pipeline> pipeline = ComponentFactory.getPipeComponent(component.getName(), component.getClazz(), component.getConfig());
                    builder.addPipeline(pipeline.getCore());
                    logger.info("Add Pipeline: {}", component.getName());
                } else {
                    throw new RuntimeException("unknown component type: " + component.getType());
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        });
        builder.setExceptionHandler((throwable, task, page) -> {
            Class exceptionClazz = throwable.getClass();
            if (exceptionClazz == FetcherNotFoundException.class
                    || exceptionClazz == ProcessorNotFoundException.class
                    || exceptionClazz == PipelineNotFoundException.class) {
                logger.warn("Component not found when handle {}, cause by {}", task, exceptionClazz.getSimpleName());
            } else {
                if (exceptionClazz == FetchException.class
                        || exceptionClazz == ProcessException.class
                        || exceptionClazz == PipeException.class) {

                    if (exceptionClazz == FetchException.class && page != null && page.getStatusCode() > 400) {
                        logger.warn("Fetcher return status [{}] when handle [{}]", page.getStatusCode(), task);
                        throwable.printStackTrace();
                    }
//                    Gospy thisSpider = Spiders.gospyMap.get(groupId);
//                    thisSpider.stop();
//                    taskGroup.setStatus(1);
//                    taskGroup.setLastModifyTime(System.currentTimeMillis());

                    taskGroupDao.updateTaskGroupById(taskGroup);
                }
            }
            return Arrays.asList();
        });
        Gospy gospy = builder.build();
        logger.info("Spider core has successfully created.");
        taskDao.getTasksByGroupId(groupId).forEach(task -> gospy.addTask(task.getUrl()));
        Spiders.gospyMap.put(groupId, gospy);
    }

    public void unbindSpider(Spider spider) {
        spider = spiderDao.getSpiderById(spider.getId());
        if (spider.getGroupId() != 0) {
            Spiders.gospyMap.remove(spider.getGroupId());

            TaskGroup taskGroup = taskGroupDao.getTaskGroupById(spider.getGroupId());
            taskGroup.setStatus(0);
            taskGroup.setLastModifyTime(System.currentTimeMillis());
            taskGroupDao.updateTaskGroupById(taskGroup);

            spider.setGroupId(0);
            spiderDao.updateSpiderById(spider);
        }
    }

    public void unbindSpider(int groupId) {
        List<Spider> spiders = spiderDao.listSpiders();
        for (Spider spider : spiders) {
            if (spider.getGroupId() == groupId) {
                unbindSpider(spider);
                return;
            }
        }
        throw new RuntimeException("no spider[group=" + groupId + "] found.");
    }

}
