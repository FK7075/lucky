package com.lucky.v2.aop;

/**
 * @Description: JDK动态代理和cglib动态代理抽象出公共部分的接口去获取代理对象
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 11:36
 */
public interface AopProxy {

    //获取代理对象
    Object getProxy();

    //通过类加载器获取代理对象
    Object getProxy(ClassLoader classLoader);
}
