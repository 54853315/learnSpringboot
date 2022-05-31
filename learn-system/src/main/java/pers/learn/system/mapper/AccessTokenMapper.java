package pers.learn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pers.learn.system.entity.AccessToken;

import java.util.List;

@Mapper
public interface AccessTokenMapper extends BaseMapper<AccessToken> {
    /**
     * 查询过期会话集合
     *
     * @param lastAccessTime 过期时间
     * @return 会话集合
     */
    @Select("select session_id, login_name, access_token, ipaddr,  status, create_time, last_access_time, expire_time" +
            " from access_token where o.last_access_time = #{lastAccessTime} ORDER BY o.last_access_time ASC")
    public List<AccessToken> selectTokenByExpired(String lastAccessTime);
}
