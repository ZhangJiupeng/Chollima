package cc.gospy.chollima.entity.bean.mapper;

import cc.gospy.chollima.entity.bean.Task;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface TaskMapper {
    @Results(id = "TaskResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "groupId", column = "gid"),
            @Result(property = "url", column = "url")
    })
    @Select("select * from tasks")
    List<Task> selectAll();

    @ResultMap("TaskResult")
    @Select("select * from tasks where id = #{id}")
    Task selectById(int id);

    @Insert("insert into tasks(url, gid) values(#{url}, #{groupId})")
    void insert(Task task);

    @Delete("delete from tasks where gid = #{groupId}")
    void deleteByGroupId(int groupId);

    @ResultMap("TaskResult")
    @Select("select * from tasks where gid = #{groupId}")
    List<Task> selectAllByGroupId(int groupId);

    @Select("select count(*) from tasks where gid = #{groupId}")
    int selectTaskCountByGroupId(int groupId);

    @Insert("<script>" +
            "insert into tasks(url, gid) values " +
            "<foreach collection=\"list\" item=\"task\" index=\"index\" separator=\",\">" +
            "(#{task.url}, #{task.groupId})" +
            "</foreach>" +
            "</script>")
    void insertAll(List<Task> tasks);

}
