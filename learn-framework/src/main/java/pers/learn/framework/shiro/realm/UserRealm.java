package pers.learn.framework.shiro.realm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import pers.learn.framework.shiro.service.ShiroTokenService;
import pers.learn.framework.shiro.token.UserBearerToken;
import pers.learn.framework.shiro.token.UserUsernamePasswordToken;
import pers.learn.system.entity.User;
import pers.learn.system.service.impl.UserServiceImpl;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ShiroTokenService shiroTokenService;

    //    @Override
    public boolean supports(AuthenticationToken token) {
        // 同时支持用token和用户密码登录subject
        if (token instanceof UserBearerToken || token instanceof UserUsernamePasswordToken) {
            return true;
        }
        return false;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = authenticationToken.getPrincipal().toString();
        // 获取用户信息
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getName, username)
                .last("LIMIT 1");
        User user = userService.getOne(wrapper);
        if (user != null) {
            if (authenticationToken instanceof UserUsernamePasswordToken) {
                return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
            } else {
                String accessToken = authenticationToken.getCredentials().toString();
                if (isTokenOnline(accessToken)) {
                    // 用户已经登录过，拥有token，它用token访问Shiro时，Shiro不认识它、不记得它，所以我们写了一个Filter让有Authorization的访问再次触发本方法（doGetAuthorizationInfo）
                    // 这时我们就需要返回用户实体信息，方便后续权限判断能够通过subject获得用户信息做更进一步的事情
                    return new SimpleAuthenticationInfo(user, authenticationToken.getCredentials(), getName());
                }
            }
        }
        return null;
    }

    /**
     * 检测Token是否在数据库中，如果Token不在数据库中则代表用户被管理员手动离线或者ban了，总之不可用
     *
     * @param token
     * @return
     */
    private Boolean isTokenOnline(String token) {
        return shiroTokenService.findAccessToken(token) != null;
    }
}
