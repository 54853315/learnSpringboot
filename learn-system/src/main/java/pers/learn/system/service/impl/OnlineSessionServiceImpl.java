package pers.learn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.learn.common.constant.Shiro;
import pers.learn.common.util.DateAdopter;
import pers.learn.system.entity.OnlineSession;
import pers.learn.system.mapper.OnlineSessionMapper;
import pers.learn.system.service.OnlineSessionService;

import java.io.Serializable;
import java.util.Date;
import java.util.Deque;
import java.util.List;

@Service
public class OnlineSessionServiceImpl extends ServiceImpl<OnlineSessionMapper, OnlineSession> implements OnlineSessionService {

    @Autowired
    private OnlineSessionMapper accessTokenMapper;

    @Autowired
    private EhCacheManager ehCacheManager;

    @Override
    public OnlineSession getBySessionId(@NonNull String sessionId) {
        LambdaQueryWrapper<OnlineSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OnlineSession::getSessionId, sessionId);
        return accessTokenMapper.selectOne(wrapper);
    }

    /**
     * 清理用户缓存
     * ! 未测试
     * @param loginName
     * @param sessionId
     */
    @Override
    public void removeUserCache(String loginName, String sessionId) {
        Cache<String, Deque<Serializable>> cache = ehCacheManager.getCache(Shiro.BACKEND_AUTH_CACHE);
        Deque<Serializable> deque = cache.get(loginName);
        if (deque == null || deque.size() == 0) {
            return;
        }
        deque.remove(sessionId);
    }

    @Override
    public List<OnlineSession> selectTokenByExpired(Date expiredDate) {
        return accessTokenMapper.selectSessionByExpired(DateAdopter.parseDateToStr(expiredDate));
    }
}
