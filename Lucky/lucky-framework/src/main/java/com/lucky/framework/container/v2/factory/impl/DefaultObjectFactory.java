package com.lucky.framework.container.v2.factory.impl;

import com.lucky.framework.container.v2.factory.ObjectFactory;
import com.lucky.utils.reflect.ClassUtils;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/4 0004 15:08
 */
public class DefaultObjectFactory implements ObjectFactory {
    @Override
    public Object getBean(Class<?> aClass) {
        return ClassUtils.newObject(aClass);
    }
}
