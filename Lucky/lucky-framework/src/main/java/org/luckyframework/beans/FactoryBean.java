package org.luckyframework.beans;

import org.luckyframework.beans.factory.BeanFactory;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/9 0009 11:47
 */
public interface FactoryBean<T> {

     T getBean();

     boolean needBeanFactory();

     void setBeanFactory(BeanFactory beanFactory);
}
