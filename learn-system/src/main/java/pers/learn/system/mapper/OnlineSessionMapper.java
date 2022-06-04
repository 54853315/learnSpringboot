package pers.learn.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pers.learn.system.entity.OnlineSession;

import java.util.List;

@Mapper
public interface OnlineSessionMapper extends BaseMapper<OnlineSession> {
    /**
     * 查询过期会话集合
     *
     * @param lastAccessTime 过期时间
     * @return 会话集合
     */
    @Select("select session_id, login_name, access_token, ipaddr,  status, create_time, last_access_time, expire_time" +
            " from online_session where o.last_access_time = #{lastAccessTime} ORDER BY o.last_access_time ASC")
    public List<OnlineSession> selectSessionByExpired(String lastAccessTime);
}
