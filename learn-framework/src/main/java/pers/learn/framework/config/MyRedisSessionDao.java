package pers.learn.framework.config;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import io.lettuce.core.RedisClient;

public class MyRedisSessionDao extends AbstractSessionDAO{
    private static final int SESSION_EXPIRE = 60 * 30;
    @Resource
    private RedisClient redisClient;
    @Override
    public void update(Session session) throws UnknownSessionException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(Session session) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Collection<Session> getActiveSessions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        // byte[] keyByte = RedisKey.getKeyB

        return null;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
