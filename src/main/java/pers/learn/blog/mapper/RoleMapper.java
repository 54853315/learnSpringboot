package pers.learn.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import pers.learn.blog.entity.Role;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    // Role getRoleWithPermissionsById(@Param("id") Long id);
}
