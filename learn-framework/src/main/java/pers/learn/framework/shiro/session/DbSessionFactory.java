package pers.learn.framework.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.web.session.mgt.WebSessionContext;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义sessionFactory会话
 * 让缓存中的session类变成DbSession
 */
public class DbSessionFactory implements SessionFactory {

    /**
     * 让缓存中的session类变成DbSession
     * @param sessionContext
     * @return
     */
    @Override
    public Session createSession(SessionContext sessionContext) {
        DbSession dbSession = new DbSession();
        dbSession.setHost(sessionContext.getHost());
        return dbSession;
    }
}
