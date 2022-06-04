package pers.learn.framework.shiro.web.token;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import pers.learn.framework.shiro.service.ShiroTokenService;
import pers.learn.framework.shiro.token.BearerAuthenticationToken;
import pers.learn.system.entity.BackendUser;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Singleton
public class BearerAuthenticationFilter extends PathMatchingFilter {

    private static final Logger log = LoggerFactory.getLogger(BearerAuthenticationFilter.class);

    @Autowired
    private ShiroTokenService shiroTokenService;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            HttpServletRequest httpRequest = WebUtils.toHttp(request);
            String authzHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
//            if (authzHeader != null && authzHeader.startsWith("Bearer ")) {
//                String tokenValue = StringUtils.substringAfter(authzHeader, " ");
//                BackendUser user = shiroTokenService.findUserByAccessToken(tokenValue);
//                if (user != null)
//                    subject.login(new BearerAuthenticationToken(user));
//            }
        }

        return true;
    }
}
