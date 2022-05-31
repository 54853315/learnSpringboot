package pers.learn.framework.shiro.web.filter.sync;

import org.apache.shiro.web.filter.PathMatchingFilter;
import pers.learn.framework.shiro.session.DbSession;
import pers.learn.framework.shiro.session.DbSessionDAO;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 同步Session数据到Db
 */
public class SyncDbSessionFilter extends PathMatchingFilter {

    private DbSessionDAO dbSessionDAO;

    public void setDbSessionDAO(DbSessionDAO dbSessionDAO) {
        this.dbSessionDAO = dbSessionDAO;
    }

    /**
     * 同步会话数据到Db
     * 一次请求最多同步一次
     * ! 需要放到Shiro过滤器之前
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     * @throws Exception
     */
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        System.out.println("onPreHandle");
        DbSession dbSession = (DbSession) request.getAttribute("online_session");
//        System.out.println();
        // 当session 停止了，就不同步
        if (dbSession != null && dbSession.getUserId() != null && dbSession.getStopTimestamp() == null) {    //stopTimestamp不为null，则代表已停止
            dbSessionDAO.syncToDb(dbSession);
        }
        return true;
    }
}
