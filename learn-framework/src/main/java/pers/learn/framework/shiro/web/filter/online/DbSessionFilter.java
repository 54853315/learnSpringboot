package pers.learn.framework.shiro.web.filter.online;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import pers.learn.common.enums.OnlineStatus;
import pers.learn.common.exception.ApiException;
import pers.learn.framework.shiro.session.DbSession;
import pers.learn.framework.shiro.session.DbSessionDAO;
import pers.learn.system.entity.BackendUser;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义访问控制
 * 用于实现：成功访问则记录Session到DB，反之，会被视为无效访问不会记录
 */
public class DbSessionFilter extends AccessControlFilter {

    private DbSessionDAO dbSessionDAO;


    public void setDbSessionDAO(DbSessionDAO dbSessionDAO) {
        this.dbSessionDAO = dbSessionDAO;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        System.out.println("-----isAccessAllowed进来啦");
        if (subject == null || subject.getSession() == null) {
            return true;
        }
        // 获取当前会话
        Session session = dbSessionDAO.readSession(subject.getSession().getId());
        if (session instanceof DbSession) {
            System.out.println("wa~~~~");
        }

        ModelMapper modelMapper = new ModelMapper();
        DbSession dbSession = modelMapper.map(session, DbSession.class);

        servletRequest.setAttribute("online_session", dbSession);

        // 如果还没有db session数据，就提取subject创建
        if (dbSession.getUserId() == null || dbSession.getUserId() == 0L) {
            BackendUser backendUser = (BackendUser) subject.getPrincipal();
            if (backendUser != null) {
                dbSession.setUserId(backendUser.getId());
                dbSession.setLoginName(backendUser.getName());
                System.out.println(dbSession);
//                    dbSession.markAttributeChanged();
            } else {
                throw new ApiException("没有获取到用户？看一下为什么会这样，这一步不应该有");
            }


//            DbSession dbSession = new DbSession();
//            dbSession.
//            servletRequest.setAttribute("online_session", dbSession);
//
//            // 如果还没有db session数据，就提取subject创建
//            if (dbSession.getUserId() == null || dbSession.getUserId() == 0L) {
//                BackendUser backendUser = (BackendUser) subject.getPrincipals();
//                if (backendUser != null) {
//                    dbSession.setUserId(backendUser.getId());
//                    dbSession.setLoginName(backendUser.getName());
//                    System.out.println(dbSession);
////                    dbSession.markAttributeChanged();
//                } else {
//                    throw new ApiException("没有获取到用户？看一下为什么会这样，这一步不应该有");
//                }
//            }
            if (dbSession.getStatus() == OnlineStatus.offline) {
                return false;
            }
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
        System.out.println("进入onAccessDenied啦！");
        Subject subject = getSubject(servletRequest, servletResponse);
        if (subject != null) {
            subject.logout();
        }
        saveRequestAndRedirectToLogin(servletRequest, servletResponse);
        return false;
    }

}
