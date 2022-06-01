package pers.learn.framework.shiro.web.session;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 从头部中获取Authorization并做为session id
 */
public class SessionManager extends DefaultWebSessionManager {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private final String authorization = "Authorization";

    /**
     * 重写获取sessionId的方法调用当前manager获取方法
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        return this.getReferencedSessionId(request, response);
    }

    /**
     * 从不同的请求信息中获取sessionId
     *
     * @param request
     * @param response
     * @return
     */
    private Serializable getReferencedSessionId(ServletRequest request, ServletResponse response) {
        String id = this.getSessionIdCookieValue(request, response);
        if (id != null) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "cookie");
        } else {
            // 获取Uri中的JSESSIONID
            id = this.getUriPathSegmentParamValue(request, "JSESSIONID");
            if (id == null) {
                // 获取请求头中的authorization值
                id = WebUtils.toHttp(request).getHeader(this.authorization);
                if (id == null) {
                    // 从参数中获取session值
                    String name = this.getSessionIdName();
                    id = request.getParameter(name);
                    if (id == null) {
                        id = request.getParameter(name.toLowerCase());
                    }
                }
            }
            if (id != null) {
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "url");
            }
        }
        if (id != null) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        }
        log.info("从请求信息中获取到的session id是:" + id);
        return id;
    }

    private String getSessionIdCookieValue(ServletRequest request, ServletResponse response) {
        if (!this.isSessionIdCookieEnabled()) {
            log.error("Session ID cookie 设置未启用");
            return null;
        } else if (!(request instanceof HttpServletRequest)) {
            log.error("当前请求不是HttpServletRequest，无法获取SessionId");
            return null;
        } else {
            HttpServletRequest httpReq = (HttpServletRequest) request;
            return this.getSessionIdCookie().readValue(httpReq, WebUtils.toHttp(response));
        }
    }

    private String getUriPathSegmentParamValue(ServletRequest request, String paramName) {
        if (!(request instanceof HttpServletRequest)) {
            return null;
        } else {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            String uri = servletRequest.getRequestURI();
            if (uri == null) {
                return null;
            } else {
                int queryStartIndex = uri.indexOf(63);
                if (queryStartIndex >= 0) {
                    uri = uri.substring(0, queryStartIndex);
                }
                int index = uri.indexOf(59);
                if (index < 0) {
                    return null;
                } else {
                    String TOKEN = paramName + "=";
                    uri = uri.substring(index + 1);
                    index = uri.lastIndexOf(TOKEN);
                    if (index < 0) {
                        return null;
                    } else {
                        uri = uri.substring(index + TOKEN.length());
                        index = uri.indexOf(59);
                        if (index >= 0) {
                            uri = uri.substring(0, index);
                        }
                        return uri;
                    }
                }
            }
        }
    }

    private String getSessionIdName() {
        String name = this.getSessionIdCookie() != null ? this.getSessionIdCookie().getName() : null;
        return name == null ? "JSESSIONID" : name;
    }
}
