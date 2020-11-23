package com.lucky.framework;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 7:04 上午
 */
public interface ApplicationContext {

    Object getBean(String id);

    <T> List<T> getBean(Class<T>...aClass);

    List<Object> getBeanByAnnotation(Class<?extends Annotation>...annotationClasses);

    List<Object> getBeans();

    boolean isIOCType(String type);

    boolean isIOCId(String id);

    boolean isIOCClass(Class<?> componentClass);
}
