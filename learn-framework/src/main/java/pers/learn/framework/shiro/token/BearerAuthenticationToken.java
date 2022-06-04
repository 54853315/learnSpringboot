package pers.learn.framework.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

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