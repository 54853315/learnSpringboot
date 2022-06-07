package pers.learn.framework.shiro.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.learn.common.constant.Auth;
import pers.learn.common.util.security.JwtUtils;
import pers.learn.system.entity.AccessToken;
import pers.learn.system.entity.BackendUser;
import pers.learn.system.entity.User;
import pers.learn.system.service.impl.AccessTokenServiceImpl;
import pers.learn.system.service.impl.BackendUserServiceImpl;
import pers.learn.system.service.impl.UserServiceImpl;

import javax.persistence.Access;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ShiroTokenService {
    @Autowired
    private AccessTokenServiceImpl accessTokenService;
    @Autowired
    private BackendUserServiceImpl backendUserService;
    @Autowired
    private UserServiceImpl userService;

    public void deleteToken(String token) {
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getAccessToken, token);
        accessTokenService.remove(wrapper);
    }

    public Object findUserByAccessToken(String token) {
        AccessToken accessToken = accessTokenService.selectByToken(token);
        if (accessToken != null) {
            if (accessToken.getEndpoint() == Auth.BACKEND_USER) {
                return backendUserService.getById(accessToken.getUserId());
            } else {
                return userService.getById(accessToken.getUserId());
            }
        }
        return null;
    }

    public AccessToken findAccessToken(String token) {
        return accessTokenService.selectByToken(token);
    }

    public void saveToken(String token, User user) {
        saveToken(token, user.getName(), user.getId(), Auth.USER);
    }

    public void saveToken(String token, BackendUser user) {
        saveToken(token, user.getName(), user.getId(), Auth.BACKEND_USER);
    }

    public void saveToken(String token, String username, Long userId, String endpoint) {
        AccessToken obj = new AccessToken();
        obj.setUserId(userId).setLoginName(username).setAccessToken(token).setExpireTime(JwtUtils.getExpireTimeForReal()).setEndpoint(endpoint);

        // @TODO 等恢复了数据源factory后，下面的时间设定可以取消
        obj.setCreateTime(LocalDateTime.now()).setLastAccessTime(LocalDateTime.now());
        LambdaQueryWrapper<AccessToken> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccessToken::getUserId, userId);
        accessTokenService.saveOrUpdate(obj, wrapper);
    }

}
