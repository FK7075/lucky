package com.lucky.framework.container.lifecycle;

import java.lang.reflect.Field;

/**
 * 组件的生命周期
 * @author fk
 * @version 1.0
 * @date 2021/3/3 0003 10:37
 */
public interface BeanLifecycle {

    /**
     * 创建实例之后执行
     */
     default void afterCreatingInstance() {

    }

    /**
     * 设置属性之前执行
     * @param field 当前正在操作的属性
     */
    default void beforeSetField(Field field){

    }

    /**
     * 设置属性之后执行
     * @param field 当前正在操作的属性
     * @param value 注入的属性值
     */
    default void afterSetField(Field field,Object value){

    }

    /**
     * 被代理之前执行
     */
    default void beforeProxy(){

    }

    /**
     * 被代理之后执行
     * @param proxyObject 代理对象
     */
    default void afterProxy(Object proxyObject){

    }

}
