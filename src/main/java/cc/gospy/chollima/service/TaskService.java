package cc.gospy.chollima.service;

import cc.gospy.chollima.dao.TaskDao;
import cc.gospy.chollima.dao.TaskGroupDao;
import cc.gospy.chollima.entity.bean.Task;
import cc.gospy.chollima.entity.bean.TaskGroup;
import cc.gospy.chollima.util.StringUtil;
import cc.gospy.core.util.UrlBundle;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {
    @Autowired
    private TaskGroupDao taskGroupDao;
    @Autowired
    private TaskDao taskDao;

    public TaskGroup addTaskGroup(String taskGroupName, String urlPattern) {
        List<String> urls;
        try {
            urls = Lists.newArrayList(UrlBundle.parse(urlPattern));
        } catch (Exception e) {
            throw new RuntimeException("illegal url pattern");
        }
        if (urls.size() > 500) {
            throw new RuntimeException("Too many results: " + urls.size());
        }
        if (taskGroupDao.nameOccupied(taskGroupName)) {
            throw new RuntimeException("Task Group '" + taskGroupName + "' has already exists");
        }
        TaskGroup taskGroup = new TaskGroup();
        taskGroup.setName(taskGroupName);
        taskGroup.setCreateTime(System.currentTimeMillis());
        taskGroup.setLastModifyTime(System.currentTimeMillis());
        taskGroup.setStatus(0);
        taskGroup = taskGroupDao.addTaskGroup(taskGroup);
        if (taskGroup == null) {
            throw new RuntimeException("add TaskGroup failure");
        }
        int groupId = taskGroup.getId();
        List<Task> tasks = new ArrayList<>();
        urls.forEach(url -> {
            Task task = new Task();
            task.setUrl(url);
            task.setGroupId(groupId);
            tasks.add(task);
//            taskDao.addTask(task);
        });
        taskDao.addTasks(tasks);
        return taskGroup;
    }

    public int getTaskCountByGroupId(int groupId) {
        return taskDao.getTaskCountByGroupId(groupId);
    }

    public void deleteTaskGroupByGroupId(int groupId) {
        taskDao.deleteTasksByGroupId(groupId);
        TaskGroup taskGroup = new TaskGroup();
        taskGroup.setId(groupId);
        taskGroupDao.deleteTaskGroup(taskGroup);
    }

    public List<Task> getTasksByGroupId(int groupId) {
        return taskDao.getTasksByGroupId(groupId);
    }

    public List<TaskGroup> getTaskGroups() {
        return taskGroupDao.listTaskGroups();
    }

    public void updateTaskGroupName(String name, int groupId) {
        taskGroupDao.updateTaskGroupName(name, groupId);
    }

    public TaskGroup mergeTaskGroups(String name, int[] groupId) {
        TaskGroup taskGroup = new TaskGroup();
        taskGroup.setName(name);
        while (taskGroupDao.nameOccupied(taskGroup.getName())) {
            taskGroup.setName(StringUtil.concatAutoIncreasedSuffix(taskGroup.getName()));
        }
        taskGroup.setCreateTime(System.currentTimeMillis());
        taskGroup.setLastModifyTime(System.currentTimeMillis());
        taskGroup.setStatus(0);
        taskGroup = taskGroupDao.addTaskGroup(taskGroup);
        int id = taskGroup.getId();
        Set<Task> tasks = new LinkedHashSet<>();
        for (int taskGroupId : groupId) {
            taskDao.getTasksByGroupId(taskGroupId).forEach(task -> {
                task.setId(0);
                task.setGroupId(id);
                tasks.add(task);
            });
        }
        taskDao.addTasks(Lists.newArrayList(tasks));
        return taskGroup;
    }
}
