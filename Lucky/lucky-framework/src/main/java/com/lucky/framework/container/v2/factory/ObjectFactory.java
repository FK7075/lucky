package com.lucky.framework.container.v2.factory;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/4 0004 14:58
 */
@FunctionalInterface
public interface ObjectFactory {

    Object  getBean(Class<?> aClass);

}
