package pers.learn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.learn.system.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
