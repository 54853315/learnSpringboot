package pers.learn.blog.realm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import pers.learn.blog.entity.BackendUser;
import pers.learn.blog.mapper.BackendUserMapper;
import pers.learn.blog.service.BackendUserService;
import pers.learn.blog.service.impl.BackendUserServiceImpl;

public class BackendUserRealm extends AuthorizingRealm {

    @Autowired
    private BackendUserServiceImpl backendUserServiceImpl;

    // 控制角色权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    // 控制登录
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
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

}
