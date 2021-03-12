package com.lucky.v2.aop.advice;

import java.lang.reflect.Method;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 10:39
 */
public interface MethodInterceptor extends Advice{

    /**
     * 实现该方法进行环绕增强
     * @param target 被增强的目标对象
     * @param method 被增强的方法
     * @param args 执行参数
     * @return 被增强方法的执行结果
     * @throws Throwable 异常
     */
    Object invoke(Object target, Method method,Object[] args) throws Throwable;
}
