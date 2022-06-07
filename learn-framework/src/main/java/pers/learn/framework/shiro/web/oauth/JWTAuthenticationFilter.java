package pers.learn.framework.shiro.web.oauth;

import com.auth0.jwt.interfaces.Claim;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import pers.learn.common.constant.Auth;
import pers.learn.common.util.security.JwtUtils;
import pers.learn.framework.shiro.token.BackendUserBearerToken;
import pers.learn.framework.shiro.token.UserBearerToken;
import pers.learn.framework.shiro.util.ShiroUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class JWTAuthenticationFilter extends AccessControlFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String token = req.getHeader("Authorization");
        if (token == null) {
            return false;
        }
        // 从token里解析出用户名，这样Realm接收的token对象结构才一致
        Map<String, Claim> clamis = JwtUtils.validateToken(token);
        if (clamis==null || clamis.get("username") == null || clamis.get("endpoint") == null) {   //如果为空，说明token验证失败, 在onAccessDenied中处理
            return false;
        }

//        String username = JwtUtils.validateToken(token);
//        if (username == null) {
//            return false;
//        }

        AuthenticationToken authenticationToken;
        if (clamis.get("endpoint").asString().equals(Auth.BACKEND_USER)) {
            authenticationToken = new BackendUserBearerToken(clamis.get("username").asString(), token);
        } else {
            authenticationToken = new UserBearerToken(clamis.get("username").asString(), token);
//        authenticationToken = new UserBearerToken(username, token);
        }
        try {
            //委托realm进行登录认证
            getSubject(servletRequest, servletResponse).login(authenticationToken);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    // 借助login初始化subject
    private void initSubject(BackendUserBearerToken token) {
        SecurityUtils.getSubject().login(token);
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        log.trace("清理了ThreadLocal");
        ShiroUtils.cleanUp();
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 前往/notLogin接口进行json信息返回提示
        redirectToLogin(request, response);
        return false;
    }
}

