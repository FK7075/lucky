package com.lucky.shiro.conf;

import com.lucky.framework.annotation.Bean;
import com.lucky.shiro.aspect.LuckyShiroAccessControlPoint;
import com.lucky.shiro.filter.LuckyFilterChainResolverFactory;
import com.lucky.shiro.filter.LuckyShiroFilter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

/**
 *
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/12 9:39 下午
 */
public abstract class LuckyShiroConfigBean {

    /**
     * 使Shiro注解生效的配置
     * @return
     */
    @Bean
    public LuckyShiroAccessControlPoint shiroAccessControlPoint(){
        return new LuckyShiroAccessControlPoint();
    }

//    /**
//     * Thymeleaf整合Shiro标签库
//     * @return
//     */
//    @Bean
//    public ShiroDialect shiroDialect(){
//        return new ShiroDialect();
//    }

    /**
     * Shiro Web环境的默认安全管理器
     * @return
     */
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager();
        //缓存管理器
        if(cacheManager()!=null) {
            securityManager.setCacheManager(cacheManager());
        }
        //授权管理器
        if(realm()!=null){
            securityManager.setRealm(realm());
        }
        //会话管理器
        if(sessionManager()!=null){
            securityManager.setSessionManager(sessionManager());
        }
        //记住我管理器
        if(rememberMeManager()!=null){
            securityManager.setRememberMeManager(rememberMeManager());
        }
        return securityManager;
    }

//    /**
//     * Shiro过滤器
//     * @param securityManager 安全管理器
//     */
//    @Bean
//    public void luckyShiroFilter(DefaultWebSecurityManager securityManager){
//        ServerConfig server= AppConfig.getDefaultServerConfig();
//        server.addFilter(new LuckyShiroFilter(securityManager,resolverFactory()), "/*");
//    }

    /**
     * 使安全管理器生效
     * @param securityManager 安全管理器
     */
    @Bean
    public void shiroInit(DefaultWebSecurityManager securityManager){
        SecurityUtils.setSecurityManager(securityManager);
    }

    /**
     * LUCKY-SHIRO请求过滤链
     * @return
     */
    protected abstract LuckyFilterChainResolverFactory resolverFactory();

    /**
     * 记住我管理器
     * @return
     */
    protected abstract RememberMeManager rememberMeManager();

    /**
     * Shiro Web环境的会话管理器
     * @return
     */
    protected abstract DefaultWebSessionManager sessionManager();

    /**
     * 授权管理器
     * @return
     */
    protected abstract Realm realm();

    /**
     * 缓存管理器
     * @return
     */
    protected abstract CacheManager cacheManager();

}
