package pers.learn.framework.shiro.realm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import pers.learn.common.constant.Auth;
import pers.learn.framework.shiro.service.ShiroTokenService;
import pers.learn.framework.shiro.token.BearerToken;
import pers.learn.framework.shiro.token.PasswordToken;
import pers.learn.system.entity.BackendUser;
import pers.learn.system.entity.Permission;
import pers.learn.system.entity.Role;
import pers.learn.system.mapper.PermissionMapper;
import pers.learn.system.service.impl.BackendUserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

public class BackendUserRealm extends AuthorizingRealm {
    @Autowired
    private BackendUserServiceImpl backendUserServiceImpl;
    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ShiroTokenService shiroTokenService;

    @Override
    public String getName() {
        return Auth.BACKEND_USER;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        // 同时支持用token和用户密码登录subject
        if (token instanceof BearerToken || token instanceof PasswordToken) {
            return true;
        }
        return false;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
        System.out.println("-------权限认证-------");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 校验当前用户类型是否正确，正确则进入处理角色权限问题，否则跳出
        // 由于Realm的身份认证是全局通用的，在这里就必须做一下实体判断
        // 因为使用了CustomModularRealmAuthorizer 验证器，这里就不需要if了
//        if (!pc.getRealmNames().contains(getName())) return null;
        BackendUser user = (BackendUser) pc.getPrimaryPrincipal();
        Role role = backendUserServiceImpl.getRoleByUser(user);
        // System.out.println("当前用户" + user);
        // System.out.println("当前用户的Role数据:" + role);
        // 设定Role
        info.addRole(role.getSign());
        if (role.isAdmin()) {
            // 管理员拥有所有角色
            info.addStringPermission("*:*:*");
        } else {
            // 设定Permissions
            LambdaQueryWrapper<Permission> permissionWrapper = new LambdaQueryWrapper<Permission>();
            List<Permission> permissionList = permissionMapper.selectList(permissionWrapper);
            info.addStringPermissions(
                    permissionList.parallelStream().map(Permission::getName).collect(Collectors.toList()));
        }
        System.out.println("Subject角色：" + info.getRoles());
        System.out.println("Subject全部权限：" + info.getStringPermissions());

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        // 获取用户信息
        LambdaQueryWrapper<BackendUser> wrapper = new LambdaQueryWrapper<BackendUser>()
                .eq(BackendUser::getName, username)
                .last("LIMIT 1");
        BackendUser backendUser = backendUserServiceImpl.getOne(wrapper);
        if (backendUser != null) {
            if (authenticationToken instanceof PasswordToken) {
                return new SimpleAuthenticationInfo(backendUser, backendUser.getPassword(), ByteSource.Util.bytes(backendUser.getSalt()), getName());
            } else {
                String accessToken = authenticationToken.getCredentials().toString();
                if (isTokenOnline(accessToken)) {
                    // 用户已经登录过，拥有token，它用token访问Shiro时，Shiro不认识它、不记得它，所以我们写了一个Filter让有Authorization的访问再次触发本方法（doGetAuthorizationInfo）
                    // 这时我们就需要返回用户实体信息，方便后续权限判断能够通过subject获得用户信息做更进一步的事情
                    return new SimpleAuthenticationInfo(backendUser, authenticationToken.getCredentials(), getName());
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
            BackendUser user = (BackendUser) principals.getPrimaryPrincipal();
            shiroTokenService.deleteTokenByUserId(user.getId(), Auth.BACKEND_USER);
            super.clearCachedAuthenticationInfo(principals);
        }
    }
}
