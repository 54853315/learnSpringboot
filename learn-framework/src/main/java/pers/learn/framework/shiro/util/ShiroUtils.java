package pers.learn.framework.shiro.util;

import pers.learn.common.constant.Auth;
import pers.learn.system.entity.BackendUser;

import java.util.HashMap;
import java.util.Map;

public class ShiroUtils {
    static private ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    public static BackendUser getCurUser() {
        return (BackendUser) threadLocal.get().get("curUser");
    }

    public static String getCurUserId() {
        return (String) threadLocal.get().get("userId");
    }

    public static String getCurUsername() {
        return (String) threadLocal.get().get(Auth.JWT_USERNAME);
    }

    public static Object getPrincipal() {
        return threadLocal.get();
    }

    public static void setCurUser(BackendUser curUser) {
        threadLocal.get().put("curUser", curUser);
    }

    public static void setPrincipal(Map<String, Object> principal) {
        threadLocal.get().put("principal", principal);
    }

    public static void cleanUp() {
        threadLocal.remove();
    }
}
