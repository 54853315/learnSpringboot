package pers.learn.framework.shiro.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@Data
@NoArgsConstructor
public abstract class AbstractUsernamePasswordToken implements AuthenticationToken {
    private String username;
    private char[] password;

    public AbstractUsernamePasswordToken(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public void clear() {
        this.username = null;
        if (this.password != null) {
            for (int i = 0; i < this.password.length; ++i) {
                this.password[i] = 0;
            }
            this.password = null;
        }

    }


    @Override
    public Object getPrincipal() {
        return this.username;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }
}
