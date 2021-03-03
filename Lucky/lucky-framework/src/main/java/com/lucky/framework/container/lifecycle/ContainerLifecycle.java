package com.lucky.framework.container.lifecycle;

import com.lucky.framework.container.SingletonContainer;

import java.util.Set;

/**
 * 容器的生命周期
 * @author fk
 * @version 1.0
 * @date 2021/3/3 0003 10:37
 */
public interface ContainerLifecycle {

    /**
     * 容器初始化之前执行
     * @param allBeanClass 收集到的所有的Bean的Class
     */
    default void beforeContainerInitialized(Set<Class<?>> allBeanClass){

    }

    /**
     * 创建每个Bean实例之前执行
     * @param beanClass 实例的Class
     * @param beanName  实例的ID
     * @param beanType  实例的类型
     */
    default void beforeCreatingInstance(Class<?> beanClass,String beanName,String beanType){

    }

    /**
     * 所有实例已经创建完成，但属性注入还未开始
     * @param singletonPool 当前的单例池(所有实例的集合)
     */
    default void instanceCreatedButNoAttributesInjected(SingletonContainer singletonPool){

    }

    /**
     * 容器初始化完成
     * @param singletonPool 当前的单例池(所有实例的集合)
     */
    default void afterContainerInitialized(SingletonContainer singletonPool){

    }




}
