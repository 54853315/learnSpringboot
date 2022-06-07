package pers.learn.framework.shiro.token;

/**
 * 前台用户免密Token登录用的结构
 */
public class UserBearerToken extends AbstractBearerToken {

    public UserBearerToken(String username, String token) {
        this.setUsername(username);
        this.setToken(token);
    }
}