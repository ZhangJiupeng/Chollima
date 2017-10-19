package cc.gospy.chollima.dao.impl;

import cc.gospy.chollima.dao.TaskDao;
import cc.gospy.chollima.entity.bean.Task;
import cc.gospy.chollima.entity.bean.TaskGroup;
import cc.gospy.chollima.entity.bean.mapper.TaskGroupMapper;
import cc.gospy.chollima.entity.bean.mapper.TaskMapper;
import cc.gospy.chollima.util.MyBatisUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskDaoImpl implements TaskDao {
    @Override
    public void addTask(Task task) {
        final boolean[] finished = {false};
        MyBatisUtil.execute(TaskMapper.class, mapper -> {
            try {
                mapper.insert(task);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }

    @Override
    public void addTasks(List<Task> taskList) {
        final boolean[] finished = {false};
        MyBatisUtil.execute(TaskMapper.class, mapper -> {
            try {
                mapper.insertAll(taskList);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }

    @Override
    public void deleteTasksByGroupId(int groupId) {
        final boolean[] finished = {false};
        MyBatisUtil.execute(TaskMapper.class, mapper -> {
            try {
                mapper.deleteByGroupId(groupId);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }

    @Override
    public List<Task> getTasksByGroupId(int groupId) {
        final boolean[] finished = {false};
        List<Task> tasks = new ArrayList<>();
        MyBatisUtil.execute(TaskMapper.class, mapper -> {
            try {
                tasks.addAll(mapper.selectAllByGroupId(groupId));
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return tasks;
    }

    @Override
    public int getTaskCountByGroupId(int groupId) {
        final boolean[] finished = new boolean[1];
        final int[] ret = {0};
        MyBatisUtil.execute(TaskMapper.class, mapper -> {
            try {
                ret[0] = mapper.selectTaskCountByGroupId(groupId);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return ret[0];
    }
}
