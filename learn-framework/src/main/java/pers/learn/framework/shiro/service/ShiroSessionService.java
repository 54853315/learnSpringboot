package pers.learn.framework.shiro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.learn.framework.shiro.session.DbSession;
import pers.learn.system.entity.OnlineSession;
import pers.learn.system.service.impl.OnlineSessionServiceImpl;

import java.io.Serializable;

/**
 * Shiro session 服务操作
 */
@Component
public class ShiroSessionService {

    @Autowired
    private OnlineSessionServiceImpl onlineSessionService;

    /**
     * 删除会话
     *
     * @param dbSession
     */
    public void deleteSession(DbSession dbSession) {
        LambdaQueryWrapper<OnlineSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OnlineSession::getSessionId, dbSession.getId());
        onlineSessionService.remove(wrapper);
    }

    /**
     * 根据sessionid获取会话信息
     *
     * @param sessionId
     * @return
     */
    public Session getSession(Serializable sessionId) {
        OnlineSession onlineSession = onlineSessionService.getBySessionId(String.valueOf(sessionId));
        return onlineSession == null ? null : createSession(onlineSession);
    }

    /**
     * 创建会话信息对象
     *
     * @param onlineSession
     * @return
     */
    public DbSession createSession(OnlineSession onlineSession) {
        DbSession dbSession = new DbSession();
        dbSession.setUserId(onlineSession.getUserId());
        dbSession.setId(onlineSession.getSessionId());
        dbSession.setHost(onlineSession.getIpaddr());
        dbSession.setLoginName(onlineSession.getLoginName());
        dbSession.setStartTimestamp(onlineSession.getCreateTime());
        dbSession.setLastAccessTime(onlineSession.getLastAccessTime());
        dbSession.setTimeout(onlineSession.getExpireTime());
        return dbSession;
    }

    /**
     * 保存session到数据库中
     *
     * @param dbSession
     */
    public void saveOnline(final DbSession dbSession) {
        OnlineSession obj = new OnlineSession(
                dbSession.getUserId(),
                dbSession.getLoginName(),
                dbSession.getHost(),
                String.valueOf(dbSession.getId()),
                dbSession.getStartTimestamp(),
                dbSession.getLastAccessTime(),
                dbSession.getTimeout(),
                dbSession.getStatus()
        );
        LambdaQueryWrapper<OnlineSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OnlineSession::getUserId, dbSession.getUserId());
        onlineSessionService.saveOrUpdate(obj, wrapper);
    }
}
