package pers.learn.framework.shiro.token;

/**
 * 管理后台用户免密Token登录用的结构
 */
public class BackendUserBearerToken extends AbstractBearerToken {

    public BackendUserBearerToken(String username, String token) {
        this.setUsername(username);
        this.setToken(token);
    }
}