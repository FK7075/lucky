package com.lucky.utils.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/24 14:39
 */
public abstract class JDKProxy {
    
    public static Object getProxy(LuckyInvocationHandler luckyInvocationHandler){
        return Proxy.newProxyInstance(luckyInvocationHandler.getClassLoader(),luckyInvocationHandler.getInterfaces(),luckyInvocationHandler);
    }

    public static boolean isAgent(Class<?> aClass){
        return Proxy.isProxyClass(aClass);
    }
}
