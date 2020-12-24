package com.lucky.shiro.filter;

import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/10 11:18
 */
public class LuckyShiroFilter extends AbstractShiroFilter {

    public LuckyShiroFilter(WebSecurityManager webSecurityManager, LuckyFilterChainResolverFactory resolverFactory){
        super();
        if (webSecurityManager == null) {
            throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
        }
        FilterChainManager manager = resolverFactory.createFilterChainManager();
        PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(manager);

        setSecurityManager(webSecurityManager);

        if (chainResolver != null) {
            setFilterChainResolver(chainResolver);
        }
    }
}
