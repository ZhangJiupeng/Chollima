package cc.gospy.chollima.api;

import cc.gospy.chollima.entity.Message;
import cc.gospy.chollima.entity.bean.Component;
import cc.gospy.chollima.entity.bean.Spider;
import cc.gospy.chollima.entity.component.Component.Type;
import cc.gospy.chollima.service.ComponentAccessService;
import cc.gospy.chollima.service.ComponentAssemblyService;
import cc.gospy.chollima.service.PagePreviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/workshop/**")
public class WorkshopController {
    private static Logger logger = LoggerFactory.getLogger(WorkshopController.class);

    @Autowired
    private ComponentAccessService componentAccessService;

    @Autowired
    private ComponentAssemblyService componentAssemblyService;

    @Autowired
    private PagePreviewService pagePreviewService;

    @RequestMapping(value = "/component/{component}/template", method = GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getTemplates(@PathVariable("component") String component) {
        switch (component) {
            case "fetcher":
                return componentAccessService.getFetcherTemplatesJson();
            case "processor":
                return componentAccessService.getProcessorTemplatesJson();
            case "pipeline":
                return componentAccessService.getPipelineTemplatesJson();
            default:
                return new Message(false, "unknown component: " + component).toJson();
        }
    }

    @RequestMapping(value = "/component/{component}/{className:.*}/template", method = GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getTemplate(@PathVariable("component") String component, @PathVariable("className") String className) {
        switch (component) {
            case "fetcher":
                return componentAccessService.getDefaultConfigJsonFromFetcherTemplates(className);
            case "processor":
                return componentAccessService.getDefaultConfigJsonFromProcessorTemplates(className);
            case "pipeline":
                return componentAccessService.getDefaultConfigJsonFromPipelineTemplates(className);
            default:
                return new Message(false, "unknown component: " + component).toJson();
        }
    }

    @RequestMapping(value = "/component/{component}/{className:.*}/form", method = GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getComponentForm(@PathVariable("className") String className, @PathVariable("component") String component) {
        return componentAccessService.getComponentForm(className);
    }

    @RequestMapping(value = "/component/{component}/{className:.*}/{name}", method = POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addComponent(@PathVariable("component") String component, @PathVariable("className") String className
            , @PathVariable("name") String name, @RequestBody String config) {
        Type type = cc.gospy.chollima.entity.component.Component.getType(component);
        if (type == null) {
            return new Message(false, "unknown component: " + component).toJson();
        }
        Component c = new Component();
        c.setName(name);
        c.setType(type.getValue());
        c.setClazz(className);
        c.setConfig(config);
        c.setReference(0);
        try {
            return new Message(componentAssemblyService.addComponent(c)).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/component/{component}", method = GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listComponents(@PathVariable("component") String component) {
        Type type = cc.gospy.chollima.entity.component.Component.getType(component);
        if (type == null) {
            return new Message(false, "unknown component: " + component).toJson();
        } else {
            return new Message(componentAssemblyService.listComponents(type)).toJson();
        }
    }

    @RequestMapping(value = "/component", method = GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listComponents() {
        return new Message(componentAssemblyService.listComponents()).toJson();
    }

    @RequestMapping(value = "/component/{id}", method = DELETE, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteComponent(@PathVariable("id") int id) {
        try {
            Component component = new Component();
            component.setId(id);
            return new Message(componentAssemblyService.deleteComponent(component)).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/spider", method = GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listSpiders() {
        try {
            return new Message(componentAssemblyService.listSpiders()).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/spider/{name}", method = POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addSpider(@PathVariable("name") String name, @RequestParam("components[]") List<Integer> componentIds) {
        try {
            Spider spider = new Spider();
            spider.setName(name);
            List<Component> components = new ArrayList<>();
            componentIds.forEach(e -> {
                Component component = new Component();
                component.setId(e);
                components.add(component);
            });
            return new Message(componentAssemblyService.addSpider(spider, components)).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/spider/{id}", method = DELETE, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteSpider(@PathVariable("id") int id) {
        try {
            Spider spider = new Spider();
            spider.setId(id);
            componentAssemblyService.deleteSpider(spider);
            return new Message(null).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/page/preview", method = RequestMethod.GET)
    @ResponseBody
    public Message getPagePreview(@RequestParam("url") String url, @RequestParam(value = "cookie", defaultValue = "") String cookie) {
        if (cookie.equals("")) {
            return pagePreviewService.getDocument(url);
        } else {
            return pagePreviewService.getDocument(url, cookie);
        }
    }

}
