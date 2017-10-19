package cc.gospy.chollima.entity.bean.mapper;

import cc.gospy.chollima.entity.bean.TaskGroup;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TaskGroupMapper {

    @Results(id = "TaskGroupResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "gname"),
            @Result(property = "status", column = "status"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "lastModifyTime", column = "modify_time")
    })
    @Select("select * from task_group")
    List<TaskGroup> selectAll();

    @ResultMap("TaskGroupResult")
    @Select("select * from task_group where id = #{id}")
    TaskGroup selectById(int id);

    @ResultMap("TaskGroupResult")
    @Select("select * from task_group where gname = #{name} and status = #{status}" +
            " and create_time = #{createTime} and modify_time = #{lastModifyTime} order by id desc limit 1")
    TaskGroup selectByValues(TaskGroup taskGroup);

    @Insert("insert into task_group(gname, status, create_time, modify_time)" +
            " values (#{name}, #{status}, #{createTime}, #{lastModifyTime})")
    void insert(TaskGroup taskGroup);

    @Delete("delete from task_group where id = #{id}")
    void deleteById(int id);

    @Select("select exists(select 1 from task_group where gname = #{name})")
    boolean nameOccupied(String name);

    @Update("update task_group set gname = #{name} where id = #{groupId} limit 1")
    void updateTaskGroupNameById(@Param("name") String name, @Param("groupId") int groupId);

    @Update("update task_group set gname = #{name}, status = #{status}, create_time = #{createTime}, modify_time = #{lastModifyTime} where id = #{id}")
    void updateById(TaskGroup taskGroup);
}
