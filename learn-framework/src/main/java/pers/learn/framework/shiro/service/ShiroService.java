package pers.learn.framework.shiro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.learn.framework.shiro.session.DbSession;
import pers.learn.system.entity.AccessToken;
import pers.learn.system.service.impl.AccessTokenServiceImpl;

import java.io.Serializable;

/**
 * Shiro 服务操作
 */
@Component
public class ShiroService {

    @Autowired
    private AccessTokenServiceImpl accessTokenService;

    /**
     * 删除会话
     *
     * @param dbSession
     */
    public void deleteSession(DbSession dbSession) {
        accessTokenService.removeById(dbSession.getId());
    }

    /**
     * 根据sessionid获取会话信息
     *
     * @param sessionId
     * @return
     */
    public Session getSession(Serializable sessionId) {
        AccessToken accessToken = accessTokenService.getBySessionId(String.valueOf(sessionId));
        return accessToken == null ? null : createSession(accessToken);
    }

    /**
     * 创建会话信息对象
     *
     * @param accessToken
     * @return
     */
    public DbSession createSession(AccessToken accessToken) {
        DbSession dbSession = new DbSession();
        dbSession.setId(accessToken.getSessionId());
        dbSession.setHost(accessToken.getIpaddr());
        dbSession.setLoginName(accessToken.getLoginName());
        dbSession.setStartTimestamp(accessToken.getCreateTime());
        dbSession.setLastAccessTime(accessToken.getLastAccessTime());
        dbSession.setTimeout(accessToken.getExpireTime());
        return dbSession;
    }

    /**
     * 保存session到数据库中
     *
     * @param dbSession
     */
    public void saveOnline(final DbSession dbSession) {
        AccessToken obj = new AccessToken(
                dbSession.getUserId(),
                dbSession.getLoginName(),
                dbSession.getHost(),
                String.valueOf(dbSession.getId()),
                dbSession.getStartTimestamp(),
                dbSession.getLastAccessTime(),
                dbSession.getTimeout(),
                dbSession.getStatus()
        );
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getUserId, dbSession.getUserId());
        accessTokenService.saveOrUpdate(obj, wrapper);
    }
}
