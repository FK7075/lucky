package com.lucky.framework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public abstract class CglibProxy {

    public static <T> T getCglibProxyObject(Class<T> clazz, MethodInterceptor methodInterceptor){
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setNamingPolicy(new LuckyNamingPolicy());
        enhancer.setCallback(methodInterceptor);
        return (T) enhancer.create();
    }
}



