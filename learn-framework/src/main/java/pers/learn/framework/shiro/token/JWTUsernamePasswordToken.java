package pers.learn.framework.shiro.token;

import org.apache.shiro.authc.UsernamePasswordToken;

public class JWTUsernamePasswordToken extends UsernamePasswordToken {
    private String token;

    public JWTUsernamePasswordToken(String token) {
        this.token = token;
    }

    public JWTUsernamePasswordToken() {
        super.setRememberMe(false);
    }

    public JWTUsernamePasswordToken(String username, String password) {
        super(username, (char[])(password != null ? password.toCharArray() : null), false, (String)null);
    }


    @Override
    public String getCredentials() {
        return this.token;
    }
}
