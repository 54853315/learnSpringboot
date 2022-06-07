package pers.learn.framework.shiro.token;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PasswordToken extends AbstractToken {
    private char[] password;
    private String loginType;

    @Override
    public String getLoginType() {
        return loginType;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }


    public PasswordToken(String username, String password, String loginType) {
        this.setUsername(username);
        this.setPassword(password != null ? password.toCharArray() : null);
        this.setLoginType(loginType);
//        super(username, password != null ? password.toCharArray() : null, loginType);
    }


}
