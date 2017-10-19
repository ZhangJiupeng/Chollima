package cc.gospy.chollima.entity.bean.mapper;

import cc.gospy.chollima.entity.bean.Component;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface ComponentMapper {

    @Results(id = "ComponentResult", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "cname"),
            @Result(property = "type", column = "ctype"),
            @Result(property = "clazz", column = "cclass"),
            @Result(property = "config", column = "config"),
            @Result(property = "reference", column = "ref")
    })
    @Select("select * from components")
    List<Component> selectAll();

    @ResultMap("ComponentResult")
    @Select("select * from components where id = #{id} limit 1")
    Component selectById(int id);

    @Insert("insert into components(cname, ctype, cclass, config, ref)" +
            " values (#{name}, #{type}, #{clazz}, #{config}, #{reference})")
    void insert(Component item);

    @Select("select exists(select 1 from components where cname = #{name} limit 1)")
    boolean nameOccupied(@Param("name") String name);

    @ResultMap("ComponentResult")
    @Select("select * from components where cname = #{name} and ctype = #{type}" +
            " and cclass = #{clazz} and config = #{config} and ref = #{reference} limit 1")
    Component selectByValues(Component item);

    @Delete("delete from components where id = #{id}")
    void deleteById(int id);

    @ResultMap("ComponentResult")
    @Select("select * from components where ctype = #{type}")
    List<Component> selectAllByType(int type);

    @Update("update components set ref = #{reference} where id = #{id}")
    void updateReference(Component item);
}
