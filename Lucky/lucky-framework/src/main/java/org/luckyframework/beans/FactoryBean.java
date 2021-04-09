package org.luckyframework.beans;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/9 0009 11:47
 */
@FunctionalInterface
public interface FactoryBean<T> {

     T getBean();
}
