package pers.learn.framework.shiro.token;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BearerToken extends AbstractToken {
    private String token;
    private String loginType;

    @Override
    public String getLoginType() {
        return loginType;
    }
    
    @Override
    public Object getCredentials() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }


    public BearerToken(String username, String password, String loginType) {
        this.setUsername(username);
        this.setToken(password);
        this.setLoginType(loginType);
//        super(username, password != null ? password.toCharArray() : null, loginType);
    }


}
