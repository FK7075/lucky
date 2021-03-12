package com.lucky.v2.aop;

/**
 *  IOC容器(bean工厂)接口:负责创建bean实例
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 11:32
 */
public interface BeanFactory {

    /**
     * 获取bean
     * @param name bean的名字
     * @return bean 实例
     * @throws Throwable
     */
    Object getBean(String name) throws Throwable;

    //注册AOP织入(注册AOP增强处理的观察者实现)
    void registerBeanPostProcessor(BeanPostProcessor bpp);
}
