package pers.learn.framework.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.netty.handler.codec.base64.Base64;
import pers.learn.framework.shiro.realm.BackendUserRealm;
import pers.learn.framework.shiro.realm.UserRealm;

@Configuration
public class ShiroConfiguration {

    /**
     * 是否开启记住我功能
     */
    @Value("${shiro.rememberMe.enabled: false}")
    private boolean rememberMe;

    /**
     * 设置Cookie的过期时间，秒为单位
     */
    @Value("${shiro.cookie.maxAge}")
    private int maxAge;

    @Bean
    public EhCacheManager getEhCacheManager() {
        EhCacheManager em = new EhCacheManager();
        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return em;
    }

    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * Filter Chain定义说明 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     */
    @Bean
    // public ShiroFilterFactoryBean shirFilter(org.apache.shiro.mgt.SecurityManager
    // securityManager) {
    public ShiroFilterFactoryBean shirFilter(
            @Qualifier("securityManager") org.apache.shiro.mgt.SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(getFilterChainDefinitionMap());
        // 设置登录接口
        shiroFilterFactoryBean.setLoginUrl("/admin/auth/login");
        // shiroFilterFactoryBean.setSuccessUrl("/admin/auth/info");
        // 未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/admin/auth/unauthorized");
        Map<String, Filter> filters = new HashMap<String, Filter>();
        shiroFilterFactoryBean.setFilters(filters);

        return shiroFilterFactoryBean;
    }

    // 拦截器配置
    public LinkedHashMap<String, String> getFilterChainDefinitionMap() {
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问

        // 配置静态资源允许访问
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");

        // filterChainDefinitionMap.put("/article/**", "roles[admin],perms[article:*]");
        // // 可以在这里写perms权限要求，但没必要
        filterChainDefinitionMap.put("/article/**", "authc");
        // 其他资源都需要授权才能访问
        filterChainDefinitionMap.put("/**", "authc");
        // 一些公共接口允许访问
        filterChainDefinitionMap.put("/guest/**", "anon");
        filterChainDefinitionMap.put("/admin/auth/unauthorized", "anon");
        filterChainDefinitionMap.put("/admin/auth/login", "anon");
        return filterChainDefinitionMap;
    }

    /**
     * 配置org.apache.shiro.web.session.mgt.DefaultWebSessionManager
     * 
     * @return
     */
    @Bean
    public DefaultWebSessionManager getSessionManager() {
        DefaultWebSessionManager manager = new DefaultWebSessionManager();
        // 设置session在内存中（shiro默认也是MemorySessionDAO)，适用于单机模式下的应用
        // manager.setSessionDAO(getMemorySessionDAO());
        // 自定义SessionDao
        manager.setSessionDAO(getSessionDAO());
        // System.out.println("dao:" + defaultWebSessionManager.getSessionDAO());
        // 删除过期的session
        manager.setDeleteInvalidSessions(true);
        // 设置全局session超时时间
        manager.setGlobalSessionTimeout(4200000);
        // 定时检查session
        manager.setSessionValidationSchedulerEnabled(true);
        // 启用cookie中存储sessionid
        manager.setSessionIdCookieEnabled(true);
        manager.setSessionIdCookie(getSimpleCookie());
        return manager;
    }

    public EnterpriseCacheSessionDAO getSessionDAO() {
        EnterpriseCacheSessionDAO enterpriseCacheSessionDAO = new EnterpriseCacheSessionDAO();
        enterpriseCacheSessionDAO.setCacheManager(getEhCacheManager());
        return enterpriseCacheSessionDAO;
    }

    /**
     * 配置org.apache.shiro.session.mgt.eis.MemorySessionDAO
     * 
     * @return
     */
    @Bean
    public MemorySessionDAO getMemorySessionDAO() {
        // 关于MemorySessionDAO阅读资料：https://blog.csdn.net/qq_36816062/article/details/110448890
        MemorySessionDAO memorySessionDAO = new MemorySessionDAO();
        memorySessionDAO.setSessionIdGenerator(javaUuidSessionIdGenerator());
        return memorySessionDAO;
    }

    @Bean
    public JavaUuidSessionIdGenerator javaUuidSessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    // 配置LifecycleBeanPostProcessor以管理shiro Bean的生命周期
    @Bean
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 设置随springboot启动的安全管理器，交给spring管理
     * 
     * @param {BackendUserRealm} backendUserRealm
     * @return {*}
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(
            @Qualifier("backendUserRealm") BackendUserRealm backendUserRealm) {
        DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
        dwsm.setRealm(backendUserRealm);
        // 是否启用记住我
        dwsm.setRememberMeManager(rememberMe ? rememberMeManager() : null);
        // !用户授权/认证信息Cache, 采用EhCache 缓存
        dwsm.setCacheManager(getEhCacheManager());
        // session管理器
        dwsm.setSessionManager(getSessionManager());
        System.out.println("安全管理器注册完毕");
        return dwsm;
    }

    /**
     * 记住我
     */
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //TODO 需要完善
        // if (StringUtils.isNotEmpty(cipherKey)) {
        //     cookieRememberMeManager.setCipherKey(Base64.decode(cipherKey));
        // } else {
        //     cookieRememberMeManager.setCipherKey(CipherUtils.generateNewKey(128, "AES").getEncoded());
        // }
        return cookieRememberMeManager;
    }

    /**
     * session自定义cookie名
     * 
     * @return
     */
    @Bean
    public SimpleCookie getSimpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName("security.session.id");
        simpleCookie.setPath("/");
        return simpleCookie;
    }

    /**
     * cookie 属性设置
     */
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setPath("/");
        cookie.setMaxAge(maxAge * 24 * 60 * 60);
        return cookie;
    }

    // 配置DefaultAdvisorAutoProxyCreator开启Controller中的shiro注解
    // DefaultAdvisorAutoProxyCreator用来扫描上下文，寻找所有的Advistor，将这些Advistor应用到符合其定义的切入点的Bean中
    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    /**
     * 开启shiro spring aop 权限注解支持，即：@RequiresPermissions(“权限code”
     * 
     * @param userRealm
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(BackendUserRealm userRealm) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(getDefaultWebSecurityManager(userRealm));
        return aasa;
    }

    // 自定义Realms之一：管理后台Realm
    @Bean
    public BackendUserRealm backendUserRealm(EhCacheManager cacheManager) {
        BackendUserRealm userRealm = new BackendUserRealm();
        // 在注册BackendUserRealm时设置采用EhCache缓存
        userRealm.setCacheManager(cacheManager);
        userRealm.setAuthorizationCacheName("backend");
        // NOTE 会先于securityManager完成注册
        System.out.println("backendUserRealm注册完毕");
        return userRealm;
    }

    // 自定义Realms之一：前台Realm
    // @Bean
    // public UserRealm userRealm(EhCacheManager cacheManager) {
    // UserRealm userRealm = new UserRealm();
    // 在注册UserRealm时设置采用EhCache缓存
    // userRealm.setCacheManager(cacheManager);
    // return userRealm;
    // }
}
