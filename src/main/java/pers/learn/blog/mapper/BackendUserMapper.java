package pers.learn.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

import pers.learn.blog.entity.BackendUser;
@Mapper
public interface BackendUserMapper extends BaseMapper<BackendUser> {

}
