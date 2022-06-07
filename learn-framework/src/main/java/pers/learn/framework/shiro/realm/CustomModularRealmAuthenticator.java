package pers.learn.framework.shiro.realm;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.realm.Realm;
import pers.learn.framework.shiro.token.AbstractToken;

import java.util.Collection;

/**
 * 自定义身份验证器，根据登录使用的Token匹配调用对应的Realm
 */
public class CustomModularRealmAuthenticator extends ModularRealmAuthenticator {
    /**
     * Realm分配策略
     * 通过realm.supports() 方法匹配对应的Realm
     *
     * @param realms
     * @param authenticationToken
     * @return
     */
    @Override
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken authenticationToken) {
        // 判断getRealms()是否返回为空
        assertRealmsConfigured();

        // 通过supports()方法，匹配对应的Realm
        Realm uniqueRealm = null;

        AbstractToken token = (AbstractToken) authenticationToken;

        // 找到当前登录人的登录类型
//        String loginType = token.getLoginType();
        for (Realm realm : realms) {
//            if (realm.getName().toLowerCase().contains(loginType) && realm.supports(token)) {
            // 这里只影响认证授权的Realm，所以不用做loginType的判断，没用
            if (realm.supports(token)) {
                uniqueRealm = realm;
                break;
            }
        }
        if (uniqueRealm == null) {
            throw new UnsupportedTokenException();
        }
        return uniqueRealm.getAuthenticationInfo(token);
    }
}
