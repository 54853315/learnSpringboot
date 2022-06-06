package pers.learn.framework.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 用于免密Token登录用的结构
 */
public class BearerAuthenticationToken implements AuthenticationToken {
    private String username;
    private String token;

    public BearerAuthenticationToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}