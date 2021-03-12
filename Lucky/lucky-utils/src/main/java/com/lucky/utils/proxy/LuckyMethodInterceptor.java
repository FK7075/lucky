package com.lucky.utils.proxy;

import com.lucky.utils.base.Assert;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/10 0010 15:00
 */
public abstract class LuckyMethodInterceptor implements MethodInterceptor {

    private Object target;
    private ClassLoader loader;
    private Class<?>[] interfaces;
    private Class<?> superClass;
    protected boolean isFirst=true;


    public Object getTarget() {
        return target;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public Class<?> getSuperClass() {
        return superClass;
    }

    public LuckyMethodInterceptor(Object target) {
       init(target);
    }

    public void init(Object target){
        Assert.notNull(target,"The object being proxied cannot be null.");
        Class<?> targetClass = target.getClass();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        superClass=targetClass;
        this.loader=targetClass.getClassLoader();
        this.interfaces=interfaces;
        this.target = target;
    }

}
