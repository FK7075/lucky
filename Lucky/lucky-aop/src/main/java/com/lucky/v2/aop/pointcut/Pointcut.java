package com.lucky.v2.aop.pointcut;

import java.lang.reflect.Method;

/**
 * 切入点
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 10:44
 */
public interface Pointcut {

    //匹配类
    boolean matchsClass(Class<?> targetClass);

    //匹配方法
    boolean matchsMethod(Method method, Class<?> targetClass);

}
