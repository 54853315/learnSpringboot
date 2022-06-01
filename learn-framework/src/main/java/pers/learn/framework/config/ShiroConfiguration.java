package pers.learn.framework.config;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pers.learn.common.constant.Shiro;
import pers.learn.common.util.security.CipherUtils;
import pers.learn.framework.shiro.realm.BackendUserRealm;
import pers.learn.framework.shiro.session.DbSessionDAO;
import pers.learn.framework.shiro.session.DbSessionFactory;
import pers.learn.framework.shiro.web.filter.online.DbSessionFilter;
import pers.learn.framework.shiro.web.filter.sync.SyncDbSessionFilter;
import pers.learn.framework.shiro.web.session.SessionManager;

@Configuration
public class ShiroConfiguration {

    /**
     * Session超时时间，单位为毫秒（默认30分钟）
     */
    @Value("${shiro.session.expireTime}")
    private int expireTime;

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

    /**
     * 设置cipherKey密钥
     */
    @Value("${shiro.cookie.cipherKey}")
    private String cipherKey;

    @Bean
    public EhCacheManager getEhCacheManager() {
        EhCacheManager em = new EhCacheManager();
        em.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
        return em;
    }

    /**
     * ShiroFilterFactoryBean 拦截资源文件
     *
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
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
        filters.put("syncDbSession", syncDbSessionFilter());
        filters.put("dbSession", dbSessionFilter());
        shiroFilterFactoryBean.setFilters(filters);

        return shiroFilterFactoryBean;
    }

    /**
     * Shiro连接约束配置，即过滤链的定义
     * Filter Chain定义说明
     * 1、一个URL可以配置多个Filter，使用逗号分隔
     * 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     *
     * @return
     */
    public LinkedHashMap<String, String> getFilterChainDefinitionMap() {
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // NOTE: authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问

        // 配置静态资源允许访问
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        // filterChainDefinitionMap.put("/article/**", "roles[admin],perms[article:*]");
        // 一些公共接口允许访问
        filterChainDefinitionMap.put("/guest/**", "anon");
        filterChainDefinitionMap.put("/admin/auth/unauthorized", "anon");
        filterChainDefinitionMap.put("/admin/auth/login", "anon");
        // 其他所有资源都需要授权才能访问
        filterChainDefinitionMap.put("/**", "user,dbSession,syncDbSession");

        return filterChainDefinitionMap;
    }

    @Bean
    public SessionManager getSessionManager() {
//        DbWebSessionManager manager = new DbWebSessionManager();
        SessionManager manager = new SessionManager();
        // 设置session在内存中（shiro默认也是MemorySessionDAO)，适用于单机模式下的应用
        // manager.setSessionDAO(getMemorySessionDAO());
        // 自定义SessionDao
        manager.setSessionDAO(getSessionDAO());
        manager.setCacheManager(getEhCacheManager());
        // 去掉 JSESSIONID
        manager.setSessionIdUrlRewritingEnabled(false);
        // 删除过期的session
        manager.setDeleteInvalidSessions(true);
        // 设置全局session超时时间 单位：微秒
        manager.setGlobalSessionTimeout((long) expireTime * 60 * 1000);
        // 定时检查session
        manager.setSessionValidationSchedulerEnabled(true);
        // 启用cookie中存储sessionid
//        manager.setSessionIdCookieEnabled(true);
//        manager.setSessionIdCookie(getSimpleCookie());
        // 自定义sessionFactory
        manager.setSessionFactory(sessionFactory());
        return manager;
    }

    /**
     * 自定义sessionFactory会话
     */
    @Bean
    public DbSessionFactory sessionFactory()
    {
        DbSessionFactory dbSessionFactory = new DbSessionFactory();
        return dbSessionFactory;
    }

    /**
     * 自定义在线用户同步过滤器
     */
    public SyncDbSessionFilter syncDbSessionFilter() {
        SyncDbSessionFilter syncDbSessionFilter = new SyncDbSessionFilter();
        syncDbSessionFilter.setDbSessionDAO(getSessionDAO());
        return syncDbSessionFilter;
    }

    /**
     * 自定义在线用户处理过滤器
     */
    public DbSessionFilter dbSessionFilter() {
        DbSessionFilter dbSessionFilter = new DbSessionFilter();
//        dbSessionFilter.setLoginUrl("/admin/auth/login");
        dbSessionFilter.setDbSessionDAO(getSessionDAO());
        return dbSessionFilter;
    }

    @Bean
    public DbSessionDAO getSessionDAO() {
        DbSessionDAO dbSessionDAO = new DbSessionDAO();
//        dbSessionDAO.setCacheManager(getEhCacheManager());
        return dbSessionDAO;
    }

    /**
     * 配置org.apache.shiro.session.mgt.eis.MemorySessionDAO
     *
     * @return
     */
//    @Bean
//    public MemorySessionDAO getMemorySessionDAO() {
//        // 关于MemorySessionDAO阅读资料：https://blog.csdn.net/qq_36816062/article/details/110448890
//        MemorySessionDAO memorySessionDAO = new MemorySessionDAO();
//        memorySessionDAO.setSessionIdGenerator(javaUuidSessionIdGenerator());
//        return memorySessionDAO;
//    }
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
    public DefaultWebSecurityManager getSecurityManager(@Qualifier("backendUserRealm") BackendUserRealm backendUserRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(backendUserRealm);
        // 是否启用记住我
        securityManager.setRememberMeManager(rememberMe ? rememberMeManager() : null);
        // !用户授权/认证信息Cache, 采用EhCache 缓存
        securityManager.setCacheManager(getEhCacheManager());
        // session管理器
        securityManager.setSessionManager(getSessionManager());
        System.out.println("安全管理器注册完毕");
        return securityManager;
    }

    /**
     * 记住我
     */
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        if (cipherKey != null) {
            cookieRememberMeManager.setCipherKey(Base64.decode(cipherKey));
        } else {
            cookieRememberMeManager.setCipherKey(CipherUtils.generateNewKey(128, "AES").getEncoded());
        }
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
     * 开启Shiro注解通知器
     * 开启shiro spring aop 权限注解支持，即：@RequiresPermissions(“权限code”
     *
     * @param userRealm
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(BackendUserRealm userRealm) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(getSecurityManager(userRealm));
        return aasa;
    }

    // 自定义Realms之一：管理后台Realm
    @Bean
    public BackendUserRealm backendUserRealm(EhCacheManager cacheManager) {
        BackendUserRealm userRealm = new BackendUserRealm();
        // 在注册BackendUserRealm时设置采用EhCache缓存
        userRealm.setCacheManager(cacheManager);
        userRealm.setAuthorizationCacheName(Shiro.BACKEND_AUTH_CACHE);
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
