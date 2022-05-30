package pers.learn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.learn.system.entity.AccessToken;

@Mapper
public interface AccessTokenMapper extends BaseMapper<AccessToken> {
}
