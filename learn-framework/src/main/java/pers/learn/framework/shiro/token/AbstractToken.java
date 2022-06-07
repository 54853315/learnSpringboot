package pers.learn.framework.shiro.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;


@NoArgsConstructor
public abstract class AbstractToken implements AuthenticationToken {
    protected String username;

    public abstract String getLoginType();

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

}
