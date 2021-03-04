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

    public static <T> T getJDKProxyObject(Class<T> clazz, InvocationHandler invocationHandler){
        Class<?>[] interfaces;
        if(Modifier.isInterface(clazz.getModifiers())){
            interfaces=new Class[]{clazz};
        }else{
            interfaces=clazz.getInterfaces();
        }
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),interfaces,invocationHandler);
    }
}
