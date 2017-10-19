package cc.gospy.chollima.entity.bean.mapper;

import cc.gospy.chollima.entity.bean.Assembly;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssemblyMapper {

    @Results(id = "AssemblyResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "spiderId", column = "sid"),
            @Result(property = "componentId", column = "cid")
    })
    @Select("select * from spider_components")
    List<Assembly> selectAll();

    @ResultMap("AssemblyResult")
    @Select("select * from spider_components where sid = #{spiderId}")
    List<Assembly> selectAllBySpiderId(int spiderId);

    @Insert("insert into spider_components(sid, cid) values (#{spiderId}, #{componentId})")
    void insert(Assembly assembly);

    @Delete("delete from spider_components where sid = #{spiderId} and cid = #{componentId} limit 1")
    void delete(Assembly assembly);

}
