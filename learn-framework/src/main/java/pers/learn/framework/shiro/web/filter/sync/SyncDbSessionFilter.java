package pers.learn.framework.shiro.web.filter.sync;

import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.learn.common.constant.Shiro;
import pers.learn.framework.shiro.session.DbSession;
import pers.learn.framework.shiro.session.DbSessionDAO;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 同步Session数据到Db
 */
public class SyncDbSessionFilter extends PathMatchingFilter {

    private static final Logger log = LoggerFactory.getLogger(SyncDbSessionFilter.class);

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
        log.info("SyncDbSessionFilter::onPreHandle");
        DbSession dbSession = (DbSession) request.getAttribute(Shiro.ONLINE_SESSION);
        // 如果session已经停止，就无需同步到db
        if (dbSession != null && dbSession.getUserId() != null && dbSession.getStopTimestamp() == null) {    //stopTimestamp不为null，则代表已停止
            dbSessionDAO.syncToDb(dbSession);
        }
        return true;
    }
}
