package pers.learn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

import pers.learn.system.entity.BackendUser;
@Mapper
public interface BackendUserMapper extends BaseMapper<BackendUser> {

}
