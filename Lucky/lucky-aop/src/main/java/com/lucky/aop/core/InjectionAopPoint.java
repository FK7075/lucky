package com.lucky.aop.core;

import java.lang.reflect.Method;

/**
 * 注入式AOP代理
 * @author fk7075
 * @version 1.0.0
 * @date 2020/10/31 11:26 上午
 */
public abstract class InjectionAopPoint extends AopPoint{

    public abstract boolean pointCutMethod(Class<?> currClass, Method currMethod);

    public abstract boolean pointCutClass(Class<?> currClass);
}
