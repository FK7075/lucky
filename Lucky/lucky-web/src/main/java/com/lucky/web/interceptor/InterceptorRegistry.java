package com.lucky.web.interceptor;

import com.lucky.web.mapping.UrlMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 拦截器注册中心
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/3 下午5:01
 */
public class InterceptorRegistry {

    private static InterceptorRegistry registry;
    private static final List<PathAndInterceptor> interceptors=new ArrayList<>(20);

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
     * 添加一个拦截器
     * @param path 拦截器要拦截的路径
     * @param interceptor 拦截器实例
     */
    public static void addHandlerInterceptor(String[] path,HandlerInterceptor interceptor){
        interceptors.add(new PathAndInterceptor(path,interceptor));
    }

    /**
     * 添加一个拦截器
     * @param pathAndInterceptor 拦截器实例和路径
     */
    public static void addHandlerInterceptor(PathAndInterceptor pathAndInterceptor){
        interceptors.add(pathAndInterceptor);
    }


    /**
     * 返回一个用于处理当前请求的拦截器链条
     * @param urlMapping 映射方法
     * @param currPath 当前的URL
     * @return
     */
    public HandlerExecutionChain getHandlerExecutionChain(UrlMapping urlMapping,String currPath){
        final List<HandlerInterceptor> interceptors = InterceptorRegistry.interceptors
                .stream().filter(pi -> pi.pathCheck(currPath))
                .sorted(Comparator.comparing(pi->pi.getPriority()))
                .map(inter -> inter.getInterceptor()).collect(Collectors.toList());
        return new HandlerExecutionChain(urlMapping,interceptors);
    }

}
