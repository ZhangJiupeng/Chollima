package cc.gospy.chollima.entity.bean.mapper;

import cc.gospy.chollima.entity.bean.Spider;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpiderMapper {

    @Results(id = "SpiderResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "sname"),
            @Result(property = "groupId", column = "gid")
    })
    @Select("select * from spiders")
    List<Spider> selectAll();

    @ResultMap("SpiderResult")
    @Select("select * from spiders where id = #{id}")
    Spider selectById(int id);

    @ResultMap("SpiderResult")
    @Select("select * from spiders where sname = #{name}")
    Spider selectByName(@Param("name") String name);

    @Select("select exists(select 1 from spiders where sname = #{name} limit 1)")
    boolean nameOccupied(@Param("name") String name);

    @Insert("insert into spiders(gid, sname) values (#{groupId}, #{name})")
    void insert(Spider spider);

    @Delete("delete from spiders where id = #{id}")
    void deleteById(int id);

    @Update("update spiders set gid = #{groupId}, sname = #{name} where id = #{id}")
    void updateById(Spider spider);
}
