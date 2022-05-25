package pers.learn.common.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;

import pers.learn.realm.BackendUserRealm;

public class AuthorizationUtils {
    /**
     * 清理所有用户授权信息缓存
     */
    public static void clearAllCachedAuthorizationInfo() {
        getBackendUserRealm().clearAllCachedAuthorizationInfo();
    }

    /**
     * 获取自定义Realm
     */
    public static BackendUserRealm getBackendUserRealm() {
        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
        return (BackendUserRealm) rsm.getRealms().iterator().next();
    }
}
