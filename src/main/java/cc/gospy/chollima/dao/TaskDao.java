package cc.gospy.chollima.dao;

import cc.gospy.chollima.entity.bean.Task;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDao {
    void addTask(Task task);

    void addTasks(List<Task> url);

    void deleteTasksByGroupId(int groupId);

    List<Task> getTasksByGroupId(int groupId);

    int getTaskCountByGroupId(int groupId);
}
