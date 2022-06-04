package pers.learn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.learn.common.util.DateAdopter;
import pers.learn.system.entity.AccessToken;
import pers.learn.system.mapper.AccessTokenMapper;
import pers.learn.system.service.AccessTokenService;

import java.util.Date;
import java.util.List;

@Service
public class AccessTokenServiceImpl extends ServiceImpl<AccessTokenMapper, AccessToken> implements AccessTokenService {
    @Autowired
    private AccessTokenMapper accessTokenMapper;

    @Override
    public AccessToken selectByToken(String token) {
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getAccessToken, token);
        return this.getOne(wrapper);
    }

    @Override
    public void removeUserCache(String loginName, String token) {

    }

    @Override
    public List<AccessToken> selectTokenByExpired(Date expiredDate) {
        return accessTokenMapper.selectTokensByExpired(DateAdopter.parseDateToStr(expiredDate));
    }
}
