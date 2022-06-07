package pers.learn.framework.shiro.token;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@Data
@NoArgsConstructor
public abstract class AbstractBearerToken implements AuthenticationToken {
    private String username;
    private String token;

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
