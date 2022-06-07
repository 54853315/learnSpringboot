package pers.learn.framework.shiro.token;

public class UserUsernamePasswordToken extends AbstractUsernamePasswordToken {
    public UserUsernamePasswordToken(String username, String password) {
        super(username, password != null ? password.toCharArray() : null);
    }
}
