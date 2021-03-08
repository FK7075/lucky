package com.lucky.utils.proxy;

import com.lucky.utils.base.Assert;
import jdk.nashorn.internal.ir.CallNode;

import java.lang.reflect.InvocationHandler;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/6 上午4:04
 */
public abstract class LuckyInvocationHandler implements InvocationHandler {

    private Object target;
    private ClassLoader loader;
    private Class<?>[] interfaces;

    public Object getTarget() {
        return target;
    }

    public ClassLoader getClassLoader() {
        return loader;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public void init(Object target){
        Assert.notNull(target,"The object being proxied cannot be null.");
        Class<?> targetClass = target.getClass();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if(Assert.isEmptyArray(interfaces)){
            throw new IllegalArgumentException("`"+targetClass+"` is not an implementation class for any interface！ When using the dynamic proxy technology of JDK, the object being proxied must be an implementation class of an interface!");
        }
        this.loader=targetClass.getClassLoader();
        this.interfaces=interfaces;
        this.target = target;
    }

    public LuckyInvocationHandler(Object target) {
        init(target);
    }
}
