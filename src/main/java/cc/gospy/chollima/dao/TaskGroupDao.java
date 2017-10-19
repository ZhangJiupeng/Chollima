package cc.gospy.chollima.dao;

import cc.gospy.chollima.entity.bean.TaskGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskGroupDao {
    TaskGroup addTaskGroup(TaskGroup group);

    void deleteTaskGroup(TaskGroup group);

    boolean nameOccupied(String name);

    List<TaskGroup> listTaskGroups();

    void updateTaskGroupName(String name, int groupId);

    void updateTaskGroupById(TaskGroup group);

    TaskGroup getTaskGroupById(int id);
}
