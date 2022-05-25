package pers.learn.system.mapper;

import java.util.List;

import pers.learn.entity.Message;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MessageMapper {
    @Select("select * from message where name like '%#{name}%' order by id desc")
    List<Message> listByName(@Param("name") String name);

    @Select("select * from message order by id desc")
    List<Message> list();

    @Select("select * from message where id = #{id}")
    Message getMessageById(@Param("id") Integer id);

    @Insert("insert into message(id,name,content,url) values(NULL,#{name},#{content},#{url})")
    void insertMessage(@Param("name") String name, @Param("content") String content, @Param("url") String url);
}
