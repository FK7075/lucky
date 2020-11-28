package com.lucky.framework.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/24 14:39
 */
public abstract class JDKProxy {

    public static <T> T getJDKProxyObject(Class<T> clazz, InvocationHandler invocationHandler){
       return (T) Proxy.newProxyInstance(clazz.getClassLoader(),clazz.getInterfaces(),invocationHandler);
    }
}
