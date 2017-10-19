package cc.gospy.chollima.service;

import cc.gospy.chollima.dao.TaskGroupDao;
import cc.gospy.chollima.entity.bean.TaskGroup;
import cc.gospy.chollima.repo.Spiders;
import cc.gospy.core.Gospy;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpiderService {

    @Autowired
    private TaskGroupDao taskGroupDao;

    public SpiderService() throws ClassNotFoundException {
        Class.forName(Spiders.class.getName());
    }

    public void startSpider(int groupId) {
        Gospy gospy = Spiders.gospyMap.get(groupId);
        if (gospy == null) {
            throw new RuntimeException("Spider[group=" + groupId + "] not found.");
        }
        TaskGroup group = taskGroupDao.getTaskGroupById(groupId);
        group.setStatus(2);
        taskGroupDao.updateTaskGroupById(group);
        gospy.start();
    }

    public JsonObject getSpiderStatus(int groupId) {
        Gospy gospy = Spiders.gospyMap.get(groupId);
        if (gospy == null) {
            if (taskGroupDao.getTaskGroupById(groupId) == null) {
                throw new RuntimeException("Spider[group=" + groupId + "] not exists.");
            } else {
                throw new RuntimeException("Spider[group=" + groupId + "] is stopped.");
            }
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("currentTaskQueueSize", gospy.getCurrentTaskQueueSize());
        jsonObject.addProperty("currentLazyTaskQueueSize", gospy.getCurrentLazyTaskQueueSize());
        jsonObject.addProperty("taskInputCount", gospy.getTotalTaskInputCount());
        jsonObject.addProperty("taskOutputCount", gospy.getTotalTaskOutputCount());
        jsonObject.addProperty("recodedTaskSize", gospy.getRecodedTaskSize());
        jsonObject.addProperty("runningTimeMillis", gospy.getRunningTimeMillis());
        return jsonObject;
    }

    public void stopSpider(int groupId) {
        Gospy gospy = Spiders.gospyMap.get(groupId);
        if (gospy == null) {
            throw new RuntimeException("Spider[group=" + groupId + "] not found.");
        }
        TaskGroup group = taskGroupDao.getTaskGroupById(groupId);
        if (group == null) {
            throw new RuntimeException("Task[group=" + groupId + "] not exists.");
        }
        group.setStatus(1);
        group.setLastModifyTime(System.currentTimeMillis());
        taskGroupDao.updateTaskGroupById(group);
        gospy.stop();
    }
}
