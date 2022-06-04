package pers.learn.framework.shiro.web.token;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import pers.learn.framework.shiro.service.ShiroTokenService;
import pers.learn.framework.shiro.token.BearerAuthenticationToken;
import pers.learn.system.entity.BackendUser;

import javax.inject.Singleton;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Singleton
public class BasicAuthenticationFilter extends PathMatchingFilter {
    @Autowired
    private ShiroTokenService shiroTokenService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            HttpServletRequest httpRequest = WebUtils.toHttp(request);
            String authzHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (authzHeader != null && authzHeader.toLowerCase().startsWith("basic ")) {
                String authValue = StringUtils.substringAfter(authzHeader, " ");
                String decoded = Base64.decodeToString(authValue);
                String userName = StringUtils.substringBefore(decoded, ":").trim();
                String password = StringUtils.substringAfter(decoded, ":").trim();
                if (userName.length() != 0 && password.length() != 0) {
                    BackendUser user = shiroTokenService.findUserByAccessToken(password);
                    AuthenticationToken token;
//                    if (user != null)
//                        token = new BearerAuthenticationToken(user);
//                    else
//                        token = new UsernamePasswordToken(userName, password);
//                    subject.login(token);
                }
            }
        }

        return true;
    }

//    @Override
//    protected void cleanup(ServletRequest request, ServletResponse response, Exception existing)
//            throws ServletException, IOException {
//
//        HttpServletResponse httpResponse = WebUtils.toHttp(response);
//        if (existing != null && !httpResponse.isCommitted()) {
//            ExceptionUtils.handle(httpResponse, existing);
//            existing = null;
//        }
//
//        super.cleanup(request, response, existing);
//    }

}
