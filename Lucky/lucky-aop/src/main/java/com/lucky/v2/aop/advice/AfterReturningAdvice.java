package com.lucky.v2.aop.advice;

import java.lang.reflect.Method;

/**
 * 后置增强
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 10:36
 */
public interface AfterReturningAdvice extends Advice{

    /**
     * 实现该方法进行后置增强
     * @param target 被增强的目标对象
     * @param method 被增强的方法
     * @param args 执行参数
     * @throws Throwable 异常
     */
    void afterReturning(Object target, Method method,Object[] args,Object returnValue) throws Throwable;
}
