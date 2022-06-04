package pers.learn.framework.shiro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.learn.common.util.security.JwtUtils;
import pers.learn.system.entity.AccessToken;
import pers.learn.system.entity.BackendUser;
import pers.learn.system.service.impl.AccessTokenServiceImpl;
import pers.learn.system.service.impl.BackendUserServiceImpl;

import javax.persistence.Access;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ShiroTokenService {
    @Autowired
    private AccessTokenServiceImpl accessTokenService;
    @Autowired
    private BackendUserServiceImpl backendUserService;

    public void deleteToken(String token) {
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getAccessToken, token);
        accessTokenService.remove(wrapper);
    }

    public BackendUser findUserByAccessToken(String token) {
        AccessToken accessToken = accessTokenService.selectByToken(token);
        return backendUserService.getById(accessToken.getUserId());
    }

    public void saveToken(String token, BackendUser user) {
        AccessToken obj = new AccessToken();
        obj.setAccessToken(token).setUserId(user.getId()).setExpireTime(JwtUtils.getExpireTimeForReal()).setLoginName(user.getName());
        // @TODO 等恢复了数据源factory后，下面的时间设定可以取消
        obj.setCreateTime(LocalDateTime.now()).setLastAccessTime(LocalDateTime.now());
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getUserId, user.getId());
        accessTokenService.saveOrUpdate(obj, wrapper);
    }

}
