package pers.learn.framework.shiro.web.token;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import pers.learn.common.util.security.JwtUtils;
import pers.learn.framework.shiro.token.BearerAuthenticationToken;
import pers.learn.framework.shiro.token.JWTUsernamePasswordToken;
import pers.learn.framework.shiro.util.ShiroUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
public class JWTAuthenticationFilter extends AccessControlFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String jwt = req.getHeader("Authorization");        //可以使用userid
        String username = req.getHeader("username");
        Map<String, Object> some = JwtUtils.validateToken(jwt);
        if (some!=null) {
            BearerAuthenticationToken statelessToken = new BearerAuthenticationToken(username, jwt);
            try {                //委托realm进行登录认证
                getSubject(servletRequest, servletResponse).login(statelessToken);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;


//        // 从头信息中获取jwt token
//        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
//        String token = httpRequest.getHeader("Authorization");
//        if (token == null) {
//            log.trace("token为空");
//            return false;
//        }
//        Map<String, Object> principal;
//        principal = JwtUtils.verifierToken(token);
//        if (principal == null) {  // token验证失败, 在onAccessDenied中处理
//            return false;
//        } else {
//            ShiroUtils.setPrincipal(principal);
//            // 因为禁用了session, 所以只能自己手动在每次请求通过login方法初始化subject
//            initSubject(new JWTUsernamePasswordToken(token));
//            return true;
//        }
    }

    // 借助login初始化subject
    private void initSubject(JWTUsernamePasswordToken token) {
        SecurityUtils.getSubject().login(token);
    }

//    @Override
//    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
//        redirectToLogin(servletRequest, servletResponse);
//        return false;
//    }

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        log.trace("清理了ThreadLocal");
        ShiroUtils.cleanUp();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        redirectToLogin(request, response);
        return false;
    }
}

