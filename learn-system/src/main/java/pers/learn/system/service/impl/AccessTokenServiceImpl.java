package pers.learn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.learn.system.entity.AccessToken;
import pers.learn.system.mapper.AccessTokenMapper;
import pers.learn.system.service.AccessTokenService;

@Service
public class AccessTokenServiceImpl extends ServiceImpl<AccessTokenMapper, AccessToken> implements AccessTokenService {

    @Autowired
    private AccessTokenMapper accessTokenMapper;

    @Override
    public AccessToken getBySessionId(@NonNull String sessionId) {
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getSessionId,sessionId);
        return accessTokenMapper.selectOne(wrapper);
    }
}
