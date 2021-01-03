package com.lucky.web.interceptor;

import java.util.HashSet;
import java.util.Set;

/**
 * 拦截器注册中心
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/3 下午5:01
 */
public class InterceptorRegistry {
    private static InterceptorRegistry registry;
    private static Set<HandlerInterceptor> interceptors=new HashSet<>(20);

    private InterceptorRegistry(){}

    /**
     * 返回拦截器注册中心实例
     * @return
     */
    public static InterceptorRegistry create(){
        if(registry==null){
            registry=new InterceptorRegistry();
        }
        return registry;
    }

    /**
     * 注册一个拦截器
     * @param interceptor 拦截器实例
     */
    public void addInterceptor(HandlerInterceptor interceptor){
        interceptors.add(interceptor);
    }
}
