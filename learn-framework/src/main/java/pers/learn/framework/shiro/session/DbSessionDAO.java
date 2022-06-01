package pers.learn.framework.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pers.learn.common.enums.OnlineStatus;
import pers.learn.framework.shiro.service.ShiroService;

import java.io.Serializable;
import java.util.Date;

/**
 * 实现Session存入DB
 */
public class DbSessionDAO extends EnterpriseCacheSessionDAO {

    private static final Logger log = LoggerFactory.getLogger(DbSessionDAO.class);

    @Autowired
    private ShiroService shiroService;

    //上次同步数据库的时间戳
    private static final String LAST_SYNC_DB_TIMESTAMP = DbSessionDAO.class.getName() + "LAST_SYNC_DB_TIMESTAMP";

    /**
     * 同步session到数据库的周期 单位为毫秒（默认1分钟）
     */
    @Value("${shiro.session.dbSyncPeriod}")
    private int dbSyncPeriod;

    @Override
    protected Session doReadSession(Serializable sessionId) {
        log.info("DbSessionDAO::doReadSession");
        return shiroService.getSession(sessionId);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        super.update(session);
    }

    public void syncToDb(DbSession dbSession) {
        log.info("DbSessionDAO::syncToDb");
        Date lastSyncTimestamp = (Date) dbSession.getAttribute(LAST_SYNC_DB_TIMESTAMP);
        boolean needSync = true;
        if (lastSyncTimestamp != null) {
            long deltaTime = dbSession.getLastAccessTime().getTime() - lastSyncTimestamp.getTime();
            if (deltaTime < (long) dbSyncPeriod * 60 * 1000) {   // 时间差不足时，无需同步
                needSync = false;
            }
        }
        if (needSync) {
            // 更新上次同步数据库时间
            dbSession.setAttribute(LAST_SYNC_DB_TIMESTAMP, dbSession.getLastAccessTime());
            shiroService.saveOnline(dbSession);
            log.info("db & session完成同步");
        }
    }

    /**
     * 当会话过期/停止（如用户退出时）会调用
     * * ！ 注意，异常会被shiro捕获，不会出现在终端中，如遇问题请debug断点测试
     *
     * @param session
     */
    protected void doDelete(Session session) {
        log.info("DbSessionDAO::doDelete");
        DbSession dbSession = new DbSession();
        dbSession.setId(session.getId());
        dbSession.setStatus(OnlineStatus.offline);
        shiroService.deleteSession(dbSession);
    }


}
