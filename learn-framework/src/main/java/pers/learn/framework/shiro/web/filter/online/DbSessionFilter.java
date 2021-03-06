package pers.learn.framework.shiro.web.filter.online;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pers.learn.common.constant.Shiro;
import pers.learn.common.enums.OnlineStatus;
import pers.learn.common.exception.ApiException;
import pers.learn.framework.shiro.session.DbSession;
import pers.learn.framework.shiro.session.DbSessionDAO;
import pers.learn.framework.shiro.web.filter.sync.SyncDbSessionFilter;
import pers.learn.system.entity.BackendUser;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义访问控制
 * 用于实现：成功访问则记录Session到DB，反之，会被视为无效访问不会记录
 */
public class DbSessionFilter extends AccessControlFilter {

    private static final Logger log = LoggerFactory.getLogger(DbSessionFilter.class);

    private DbSessionDAO dbSessionDAO;


    public void setDbSessionDAO(DbSessionDAO dbSessionDAO) {
        this.dbSessionDAO = dbSessionDAO;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        log.info("DbSessionFilter:isAccessAllowed，允许访问，开始判断用户是否还在线");
        Subject subject = getSubject(servletRequest, servletResponse);
        if (subject.getSession() == null) {
            log.info("用户没有提供session，踢出");
            return true;
        }
        //用户未登录，但有"记住我"状态
        if (!subject.isAuthenticated() && subject.isRemembered()) {
            log.info("这是一个记住我（有信息）的访客（未授权）");
            // 严格的敏感程序不建议在这个状态下为用户获取用户状态，所以return true把它踢出去
//            return true;
            // 如果是非严格的程序，比如登录微博看看流量，那么可以给用户基于"记住我"找回登录状态
//            Session session = subject.getSession();
//            if (session == null) {
            // 在这种方法里面做初始化用户上下文的事情，比方通过查询数据库来设置session值，确保subject.isAuthenticated()就会变成true
//                shiroService.initUserContext(subject.getPrincipal().toString(), subject);
//            }
        }
        // 获取当前会话，并强制转换为DbSession实体的结构，这里的数据获取路径为：
        // A. DbSessionDAO.doReadSession->ShiroService.createSession返回的DbSession实体
        // B. DbSessionDAO.readSession从cacheSession中提取到了SimpleSession实体（使用DbSessionFactory来设定session对象的话，cache中拿到的也会是DbSession）
        DbSession dbSession = (DbSession) dbSessionDAO.readSession(subject.getSession().getId());
//        Session session =  dbSessionDAO.readSession(subject.getSession().getId());

        // 如果没有用户信息就从subject中填充
        if (dbSession.getUserId() == null || dbSession.getUserId() == 0L) {
            BackendUser backendUser = (BackendUser) subject.getPrincipal();
            if (backendUser != null) {
                dbSession.setUserId(backendUser.getId());
                dbSession.setLoginName(backendUser.getName());
            }
        }
        servletRequest.setAttribute(Shiro.ONLINE_SESSION, dbSession);
        if (dbSession.getStatus() == OnlineStatus.offline) {
            return false;
        }
        return true;
    }

    /**
     * 当访问拒绝时是否已经处理了
     * 返回true表示需要继续处理，返回false表示该拦截器实例已经处理了，将结果直接返回即可
     *
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        log.info("拒绝访问，让用户下线");
        Subject subject = getSubject(servletRequest, servletResponse);
        if (subject != null) {
            subject.logout();
        }
        // 提示前往登录接口
        saveRequestAndRedirectToLogin(servletRequest, servletResponse);
        return false;
    }

}
