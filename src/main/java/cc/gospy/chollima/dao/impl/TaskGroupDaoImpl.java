package cc.gospy.chollima.dao.impl;

import cc.gospy.chollima.dao.TaskGroupDao;
import cc.gospy.chollima.entity.bean.TaskGroup;
import cc.gospy.chollima.entity.bean.mapper.TaskGroupMapper;
import cc.gospy.chollima.util.MyBatisUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskGroupDaoImpl implements TaskGroupDao {
    @Override
    public TaskGroup addTaskGroup(TaskGroup taskGroup) {
        final boolean[] finished = new boolean[1];
        final TaskGroup[] ret = new TaskGroup[1];
        MyBatisUtil.execute(TaskGroupMapper.class, mapper -> {
            try {
                mapper.insert(taskGroup);
                ret[0] = mapper.selectByValues(taskGroup);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return ret[0];
    }

    @Override
    public void deleteTaskGroup(TaskGroup group) {
        final boolean[] finished = {false};
        MyBatisUtil.execute(TaskGroupMapper.class, mapper -> {
            try {
                mapper.deleteById(group.getId());
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }

    @Override
    public boolean nameOccupied(String name) {
        final boolean[] finished = {false};
        final boolean[] ret = {false};
        MyBatisUtil.execute(TaskGroupMapper.class, mapper -> {
            try {
                ret[0] = mapper.nameOccupied(name);
            } finally {
                finished[0] = true;
            }
        });
        return ret[0];
    }

    @Override
    public List<TaskGroup> listTaskGroups() {
        final boolean[] finished = {false};
        List<TaskGroup> taskGroups = new ArrayList<>();
        MyBatisUtil.execute(TaskGroupMapper.class, mapper -> {
            try {
                taskGroups.addAll(mapper.selectAll());
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return taskGroups;
    }

    @Override
    public void updateTaskGroupName(String name, int groupId) {
        final boolean[] finished = new boolean[1];
        MyBatisUtil.execute(TaskGroupMapper.class, mapper -> {
            try {
                if (mapper.nameOccupied(name)) {
                    throw new RuntimeException("'" + name + "' is occupied");
                }
                mapper.updateTaskGroupNameById(name, groupId);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }

    @Override
    public void updateTaskGroupById(TaskGroup group) {
        final boolean[] finished = {false};
        MyBatisUtil.execute(TaskGroupMapper.class, mapper -> {
            try {
                mapper.updateById(group);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }

    @Override
    public TaskGroup getTaskGroupById(int id) {
        final boolean[] finished = {false};
        final TaskGroup[] ret = {null};
        MyBatisUtil.execute(TaskGroupMapper.class, mapper -> {
            try {
                ret[0] = mapper.selectById(id);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return ret[0];
    }

}
