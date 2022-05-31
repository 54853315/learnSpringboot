package pers.learn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.learn.common.util.DateAdopter;
import pers.learn.system.entity.AccessToken;
import pers.learn.system.mapper.AccessTokenMapper;
import pers.learn.system.service.AccessTokenService;

import java.io.Serializable;
import java.util.Date;
import java.util.Deque;
import java.util.List;

@Service
public class AccessTokenServiceImpl extends ServiceImpl<AccessTokenMapper, AccessToken> implements AccessTokenService {

    @Autowired
    private AccessTokenMapper accessTokenMapper;

    @Autowired
    private EhCacheManager ehCacheManager;

    @Override
    public AccessToken getBySessionId(@NonNull String sessionId) {
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getSessionId, sessionId);
        return accessTokenMapper.selectOne(wrapper);
    }

    /**
     * 清理用户缓存
     *
     * @param loginName
     * @param sessionId
     */
    @Override
    public void removeUserCache(String loginName, String sessionId) {
        Cache<String, Deque<Serializable>> cache = ehCacheManager.getCache("sys-userCache");
        Deque<Serializable> deque = cache.get(loginName);
        if (deque == null || deque.size() == 0) {
            return;
        }
        deque.remove(sessionId);
    }

    @Override
    public List<AccessToken> selectTokenByExpired(Date expiredDate) {
        return accessTokenMapper.selectTokenByExpired(DateAdopter.parseDateToStr(expiredDate));
    }
}
