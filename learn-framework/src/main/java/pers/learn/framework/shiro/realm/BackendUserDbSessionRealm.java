package pers.learn.framework.shiro.realm;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import pers.learn.system.entity.BackendUser;
import pers.learn.system.entity.Permission;
import pers.learn.system.entity.Role;
import pers.learn.system.mapper.PermissionMapper;
import pers.learn.system.service.impl.BackendUserServiceImpl;

/**
 * 后端用户Realm领域
 * Session版
 */
@Slf4j
public class BackendUserDbSessionRealm extends AuthorizingRealm {

    @Autowired
    private BackendUserServiceImpl backendUserServiceImpl;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    /**
     * 控制角色权限
     * 此方法调用 hasRole,hasPermission的时候才会进行回调.
     * 权限信息.(授权):
     * 1、如果用户正常退出，缓存自动清空；
     * 2、如果用户非正常退出，缓存自动清空；
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。
     * （需要手动编程进行实现；放在service进行调用）
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例，调用clearCached方法
     *
     * 当没有使用缓存的时候，不断刷新页面的话，请求权限判断时这个方法会不断执行。
     *
     * @param {PrincipalCollection} pc
     * @return AuthorizationInfo
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        BackendUser user = (BackendUser) pc.getPrimaryPrincipal();
        Role role = backendUserServiceImpl.getRoleByUser(user);
        log.trace(String.format("当前用户[%s]，Role为[%s]", user, role));
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
        log.trace(String.format("Subject角色[%s] Subject全部权限[%s]", info.getRoles(), info.getStringPermissions()));
        return info;
    }

    // 控制登录
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("token" + token);
        LambdaQueryWrapper<BackendUser> wrapper = new LambdaQueryWrapper<BackendUser>()
                .eq(BackendUser::getName, token.getPrincipal().toString())
                .last("LIMIT 1");
        BackendUser backendUser = backendUserServiceImpl.getOne(wrapper);
        if (backendUser != null) {
            // SimpleAuthenticationInfo中的password会跟AuthenticationToken中的credentials进行对比
            AuthenticationInfo info = new SimpleAuthenticationInfo(backendUser, backendUser.getPassword(), getName());
            return info;
        }
        return null;
    }

    /**
     * 清理指定用户授权信息缓存
     */
    public void clearCachedAuthorizationInfo(Object principal) {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
        this.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 清理所有用户授权信息缓存
     */
    public void clearAllCachedAuthorizationInfo() {
    }

}
