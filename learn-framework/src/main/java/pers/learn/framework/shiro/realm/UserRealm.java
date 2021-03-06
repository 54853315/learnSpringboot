package pers.learn.framework.shiro.realm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import pers.learn.common.constant.Auth;
import pers.learn.framework.shiro.service.ShiroTokenService;
import pers.learn.framework.shiro.token.BearerToken;
import pers.learn.framework.shiro.token.PasswordToken;
import pers.learn.framework.shiro.util.ShiroUtils;
import pers.learn.system.entity.BackendUser;
import pers.learn.system.entity.User;
import pers.learn.system.service.impl.UserServiceImpl;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private ShiroTokenService shiroTokenService;

    @Override
    public String getName() {
        return Auth.USER;
    }

    //    @Override
    public boolean supports(AuthenticationToken token) {
        // 同时支持用token和用户密码登录subject
        if (token instanceof BearerToken || token instanceof PasswordToken) {
            return true;
        }
        return false;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
        return null;
        // 校验当前用户类型是否正确，正确则进入处理角色权限问题，否则跳出
        // 由于Realm的身份认证是全局通用的，在这里就必须做一下实体判断
//        if (!pc.getRealmNames().contains(getName())) return null;
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
            if (authenticationToken instanceof PasswordToken) {
                // debug技巧：如果前台提示密码不正确，去HashedCredentialsMatcher 中断点 doCredentialsMatch()
                // doCredentialsMatch() 第一行,将用户输入的明文密码根据配置里的加密规则和数据库里的盐值进行加密
                return new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getSalt()), getName());
            } else {
                String accessToken = authenticationToken.getCredentials().toString();
                if (isTokenOnline(accessToken)) {
                    // 设置bearer token只需要普通的身份验证，不需要解密
                    setCredentialsMatcher(new SimpleCredentialsMatcher());
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

    @Override
    protected void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        if (principals.getRealmNames().contains(getName())) {
            User user = (User) principals.getPrimaryPrincipal();
            shiroTokenService.deleteTokenByUserId(user.getId(), Auth.USER);
            super.clearCachedAuthenticationInfo(principals);
        }
    }
}
