package pers.learn.framework.shiro.token;


import org.apache.shiro.authc.UsernamePasswordToken;

//public class BackendUserUsernamePasswordToken extends AbstractUsernamePasswordToken {
public class BackendUserUsernamePasswordToken extends UsernamePasswordToken {
    public BackendUserUsernamePasswordToken(String username, String password) {
        super(username, password != null ? password.toCharArray() : null);
    }
}