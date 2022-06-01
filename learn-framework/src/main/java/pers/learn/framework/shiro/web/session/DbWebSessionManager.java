package pers.learn.framework.shiro.web.session;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.learn.framework.shiro.session.DbSession;
import pers.learn.system.entity.AccessToken;
import pers.learn.system.service.AccessTokenService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 主要是在此如果会话的属性修改了 就标识下其修改了 然后方便 OnlineSessionDao同步
 * @deprecated
 */
public class DbWebSessionManager extends DefaultWebSessionManager {
    private static final Logger log = LoggerFactory.getLogger(DbWebSessionManager.class);
    @Autowired
    private AccessTokenService accessTokenService;

    @Override
    public void setAttribute(SessionKey sessionKey, Object attributeKey, Object value) throws InvalidSessionException
    {
        super.setAttribute(sessionKey, attributeKey, value);
        if (value != null && needMarkAttributeChanged(attributeKey))
        {
            DbSession session = getDbSession(sessionKey);
//            session.markAttributeChanged();
        }
    }

    private boolean needMarkAttributeChanged(Object attributeKey)
    {
        if (attributeKey == null)
        {
            return false;
        }
        String attributeKeyStr = attributeKey.toString();
        // 优化 flash属性没必要持久化
        if (attributeKeyStr.startsWith("org.springframework"))
        {
            return false;
        }
        if (attributeKeyStr.startsWith("javax.servlet"))
        {
            return false;
        }
        if (attributeKeyStr.equals("name")) //用户名字段
        {
            return false;
        }
        return true;
    }

    @Override
    public Object removeAttribute(SessionKey sessionKey, Object attributeKey) throws InvalidSessionException
    {
        Object removed = super.removeAttribute(sessionKey, attributeKey);
        if (removed != null)
        {
            DbSession s = getDbSession(sessionKey);
//            s.markAttributeChanged();
        }

        return removed;
    }

    public DbSession getDbSession(SessionKey sessionKey)
    {
        DbSession dbSession = null;
        Session obj = doGetSession(sessionKey);
        if (obj != null)
        {
            dbSession = new DbSession();
            dbSession.setId(obj.getId());
            dbSession.setHost(obj.getHost());
//            dbSession.setLoginName(obj.getLoginName());
            dbSession.setStartTimestamp(obj.getStartTimestamp());
            dbSession.setLastAccessTime(obj.getLastAccessTime());
            dbSession.setTimeout(obj.getTimeout());
        }
        return dbSession;
    }

    /**
     * 验证session是否有效 用于删除过期session
     */
    @Override
    public void validateSessions()
    {
        if (log.isInfoEnabled())
        {
            log.info("invalidation sessions...");
        }

        int invalidCount = 0;

        int timeout = (int) this.getGlobalSessionTimeout();
        if (timeout < 0)
        {
            // 永不过期不进行处理
            return;
        }
        Date expiredDate = DateUtils.addMilliseconds(new Date(), 0 - timeout);
//        AccessTokenService accessTokenService = SpringUtils.getBean(AccessTokenService.class);
        List<AccessToken> userOnlineList = accessTokenService.selectTokenByExpired(expiredDate);
        // 批量过期删除
        List<Long> needOfflineIdList = new ArrayList<Long>();
        for (AccessToken userOnline : userOnlineList)
        {
            try
            {
                SessionKey key = new DefaultSessionKey(userOnline.getSessionId());
                Session session = retrieveSession(key);
                if (session != null)
                {
                    throw new InvalidSessionException();
                }
            }
            catch (InvalidSessionException e)
            {
                if (log.isDebugEnabled())
                {
                    boolean expired = (e instanceof ExpiredSessionException);
                    String msg = "Invalidated session with id [" + userOnline.getSessionId() + "]"
                            + (expired ? " (expired)" : " (stopped)");
                    log.debug(msg);
                }
                invalidCount++;
                needOfflineIdList.add(userOnline.getId());
                accessTokenService.removeUserCache(userOnline.getLoginName(), userOnline.getSessionId());
            }

        }
        if (needOfflineIdList.size() > 0)
        {
            try
            {
                accessTokenService.removeBatchByIds(needOfflineIdList);
            }
            catch (Exception e)
            {
                log.error("batch delete db session error.", e);
            }
        }

        if (log.isInfoEnabled())
        {
            String msg = "Finished invalidation session.";
            if (invalidCount > 0)
            {
                msg += " [" + invalidCount + "] sessions were stopped.";
            }
            else
            {
                msg += " No sessions were stopped.";
            }
            log.info(msg);
        }

    }

    @Override
    protected Collection<Session> getActiveSessions()
    {
        throw new UnsupportedOperationException("getActiveSessions method not supported");
    }
}
