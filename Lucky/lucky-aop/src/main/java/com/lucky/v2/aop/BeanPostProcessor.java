package com.lucky.v2.aop;

/**
 * AOP增强处理接口
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 11:27
 */
public interface BeanPostProcessor {

    //bean初始化前增强
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws Throwable {
        return bean;
    }

    //bean初始化后增强
    default Object postProcessAfterInitialization(Object bean, String beanName) throws Throwable {
        return bean;
    }
}
