package pers.learn.framework.config;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.learn.common.constant.Shiro;
import pers.learn.framework.shiro.realm.BearerAuthorizingRealm;
import pers.learn.framework.shiro.session.DbSessionFactory;
import pers.learn.framework.shiro.session.StatelessDefaultSubjectFactory;
import pers.learn.framework.shiro.session.TokenSessionDAO;
import pers.learn.framework.shiro.web.token.JWTAuthenticationFilter;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") org.apache.shiro.mgt.SecurityManager securityManager) {
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
        // 使用token持久化过滤器
        filters.put("jwtAuth", getJwtFilter());
        shiroFilterFactoryBean.setFilters(filters);

        return shiroFilterFactoryBean;
    }


    public JWTAuthenticationFilter getJwtFilter() {
        JWTAuthenticationFilter filter = new JWTAuthenticationFilter();
        return filter;
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
        // NOTE: authc或authcBearer:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问；user指的是用户认证通过或者配置了Remember Me记住用户登录状态后可访问。

        // 配置静态资源允许访问
        filterChainDefinitionMap.put("/js/**", "noSessionCreation,anon");
        filterChainDefinitionMap.put("/css/**", "noSessionCreation,anon");
        // filterChainDefinitionMap.put("/article/**", "roles[admin],perms[article:*]");
        // 一些公共接口允许访问
        filterChainDefinitionMap.put("/guest/**", "noSessionCreation,anon");
        filterChainDefinitionMap.put("/admin/auth/unauthorized", "noSessionCreation,anon");
        filterChainDefinitionMap.put("/admin/auth/login", "noSessionCreation,anon");
        // 其他所有资源都需要授权才能访问
        filterChainDefinitionMap.put("/**", "noSessionCreation,jwtAuth"); // 这个是使用Bearer的

        return filterChainDefinitionMap;
    }

    /**
     * 自定义sessionFactory会话
     */
    @Bean
    public DbSessionFactory sessionFactory() {
        DbSessionFactory dbSessionFactory = new DbSessionFactory();
        return dbSessionFactory;
    }

    @Bean
    public TokenSessionDAO getSessionDAO() {
        TokenSessionDAO dbSessionDAO = new TokenSessionDAO();
        return dbSessionDAO;
    }

    @Bean
    public JavaUuidSessionIdGenerator javaUuidSessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    /**
     * 配置LifecycleBeanPostProcessor以管理shiro Bean的生命周期
     * ! 如果要使用@Value注解，本方法必须是static
     *
     * @return
     */
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 设置随springboot启动的安全管理器，交给spring管理
     *
     * @param {BearerAuthorizingRealm} BearerAuthorizingRealm
     * @return {*}
     */
//    @Bean(name = "securityManager")
//    public DefaultWebSecurityManager getSecurityManager(@Qualifier("BearerAuthorizingRealm") BearerAuthorizingRealm BearerAuthorizingRealm) {
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setRealm(BearerAuthorizingRealm);
//        // !用户授权/认证信息Cache, 采用EhCache 缓存
//        securityManager.setCacheManager(getEhCacheManager());
//        return securityManager;
//    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getSecurityManager(@Qualifier("BearerAuthorizingRealm") BearerAuthorizingRealm oAuthRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(oAuthRealm);
        securityManager.setSubjectFactory(new StatelessDefaultSubjectFactory());
        securityManager.setCacheManager(getEhCacheManager());
        SubjectDAO subjectDAO = securityManager.getSubjectDAO();
        if (subjectDAO instanceof DefaultSubjectDAO) {
            DefaultSubjectDAO defaultSubjectDAO = (DefaultSubjectDAO) subjectDAO;
            SessionStorageEvaluator sessionStorageEvaluator = defaultSubjectDAO.getSessionStorageEvaluator();
            if (sessionStorageEvaluator instanceof DefaultSessionStorageEvaluator) {
                DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = (DefaultSessionStorageEvaluator) sessionStorageEvaluator;
                defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
            }
        }
        //securityManager.setSessionManager(sessionManager);
        // 自定义缓存实现 使用redis
        // securityManager.setCacheManager(cacheManager());
        return securityManager;
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
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(BearerAuthorizingRealm userRealm) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(getSecurityManager(userRealm));
        return aasa;
    }

    @Bean
    public BearerAuthorizingRealm BearerAuthorizingRealm(EhCacheManager cacheManager) {
        BearerAuthorizingRealm userRealm = new BearerAuthorizingRealm();
        // 在注册BearerAuthorizingRealm时设置采用EhCache缓存
        userRealm.setCacheManager(cacheManager);
        userRealm.setAuthorizationCacheName(Shiro.BACKEND_AUTH_CACHE);
        return userRealm;
    }
}
