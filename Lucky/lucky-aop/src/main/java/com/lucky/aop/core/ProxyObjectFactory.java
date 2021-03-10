package com.lucky.aop.core;

import com.lucky.aop.proxy.LuckyAopInvocationHandler;
import com.lucky.aop.proxy.LuckyAopObjectProxyMethodInterceptor;
import com.lucky.utils.base.Assert;
import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.proxy.JDKProxy;

import java.util.List;

/**
 * 代理工厂
 * @author fk
 * @version 1.0
 * @date 2021/3/10 0010 16:34
 */
public class ProxyObjectFactory {

    private static ProxyObjectFactory proxyFactory;

    private ProxyObjectFactory() {}

    public static ProxyObjectFactory getProxyFactory() {
        if(proxyFactory==null)
            proxyFactory=new ProxyObjectFactory();
        return proxyFactory;
    }

    public Object getProxyObject(Object target, PointRun...points){
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if(Assert.isEmptyArray(interfaces)){
            return getCglibProxyObject(target,points);
        }
        return getJdkProxyObject(target,points);
    }

    public Object getProxyObject(Object target, List<PointRun> points){
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if(Assert.isEmptyArray(interfaces)){
            return getCglibProxyObject(target,points);
        }
        return getJdkProxyObject(target,points);
    }

    public Object getJdkProxyObject(Object target,List<PointRun> points){
        return JDKProxy.getProxy(new LuckyAopInvocationHandler(target,points));
    }

    public Object getCglibProxyObject(Object target,List<PointRun> points){
        return CglibProxy.getCglibProxyObject(new LuckyAopObjectProxyMethodInterceptor(target,points));
    }

    public Object getJdkProxyObject(Object target,PointRun...points){
        return JDKProxy.getProxy(new LuckyAopInvocationHandler(target,points));
    }

    public static Object getCglibProxyObject(Object target,PointRun...points){
        return CglibProxy.getCglibProxyObject(new LuckyAopObjectProxyMethodInterceptor(target,points));
    }
}
