package pers.learn.framework.config;

import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.learn.common.constant.Shiro;
import pers.learn.framework.shiro.realm.BackendUserRealm;
import pers.learn.framework.shiro.realm.CustomModularRealmAuthenticator;
import pers.learn.framework.shiro.realm.UserRealm;
import pers.learn.framework.shiro.web.oauth.JWTAuthenticationFilter;

import javax.servlet.Filter;
import java.util.*;

@Configuration
public class ShiroConfig {

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

        // 在oauth2.0 的情况下，需要设置未登录时的提示接口；如果是web session模式，则应该设置为登录页面的uri
        shiroFilterFactoryBean.setLoginUrl("/notLogin");
        // 在oauth2.0 的情况下，需要设置未授权时的提示接口；如果是web session模式，则应该设置为显示“您还没有登录呢”的403页面的uri
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        // 设置filters
        Map<String, Filter> filters = new HashMap<String, Filter>();
        filters.put("jwtAuth", new JWTAuthenticationFilter());  // 使用JWT过滤器
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
        // NOTE: authc或authcBearer:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问；user指的是用户认证通过或者配置了Remember Me记住用户登录状态后可访问。

        // 配置静态资源允许访问
        filterChainDefinitionMap.put("/js/**", "noSessionCreation,anon");
        filterChainDefinitionMap.put("/css/**", "noSessionCreation,anon");
        // filterChainDefinitionMap.put("/article/**", "roles[admin],perms[article:*]");
        // 一些公共接口允许访问
        filterChainDefinitionMap.put("/guest/**", "noSessionCreation,anon");
        filterChainDefinitionMap.put("/unauthorized", "noSessionCreation,anon");
        filterChainDefinitionMap.put("/admin/auth/login", "noSessionCreation,anon");
        filterChainDefinitionMap.put("/user/auth/login", "noSessionCreation,anon");
        filterChainDefinitionMap.put("/notLogin", "noSessionCreation,anon");
        // 其他所有资源都需要授权才能访问
        filterChainDefinitionMap.put("/**", "noSessionCreation,jwtAuth");

        return filterChainDefinitionMap;
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
     * 针对多Realm，使用自定义身份验证器
     *
     * @return
     */
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator() {
        CustomModularRealmAuthenticator authenticator = new CustomModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return authenticator;
    }

    /**
     * 设置随springboot启动的安全管理器，交给spring管理
     *
     * @param backendUserRealm
     * @param userRealm
     * @return
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getSecurityManager(@Qualifier("BackendUserRealm") BackendUserRealm backendUserRealm, @Qualifier("UserRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置验证器为自定义验证器
        securityManager.setAuthenticator(modularRealmAuthenticator());
        // 设置Realms
        List<Realm> realms = new ArrayList<>(2);
        realms.add(userRealm);
        realms.add(backendUserRealm);
        securityManager.setRealms(realms);
        // 设置缓存器
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
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(BackendUserRealm backendUserRealm, UserRealm userRealm) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(getSecurityManager(backendUserRealm, userRealm));
        return aasa;
    }
    @Bean
    public BackendUserRealm BackendUserRealm(EhCacheManager cacheManager) {
        BackendUserRealm userRealm = new BackendUserRealm();
        // 在注册BearerAuthorizingRealm时设置采用EhCache缓存
        userRealm.setCacheManager(cacheManager);
        userRealm.setAuthorizationCacheName(Shiro.BACKEND_AUTH_CACHE);
        return userRealm;
    }

    @Bean
    public UserRealm UserRealm(EhCacheManager cacheManager) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCacheManager(cacheManager);
        userRealm.setAuthorizationCacheName(Shiro.FRONT_AUTH_CACHE);
        return userRealm;
    }
}
