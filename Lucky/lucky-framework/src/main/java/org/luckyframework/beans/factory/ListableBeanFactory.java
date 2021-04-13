package org.luckyframework.beans.factory;

import com.lucky.utils.annotation.Nullable;
import org.luckyframework.beans.factory.BeanFactory;
import org.luckyframework.exception.BeansException;
import org.luckyframework.exception.NoSuchBeanDefinitionException;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/23 0023 9:20
 */
public interface ListableBeanFactory extends BeanFactory {

    /**
     * 是否包含该名称的bean定义
     * @param beanName bean定义的名称
     * @return Y/N ->T/F
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * 获取所有bean定义的数量
     * @return bean定义的总数
     */
    int getBeanDefinitionCount();

    /**
     * 获取所有bean定义的名称
     * @return 所有bean定义的名称
     */
    String[] getBeanDefinitionNames();

    /**
     * 获取所有与指定类型相匹配的bean的名称
     * @param type 指定的类型
     * @return 所有与指定类型相匹配的bean的名称
     */
    String[] getBeanNamesForType(@Nullable Class<?> type);

    /**
     * 获取所有与指定类型相匹配的bean的名称
     * @param type 指定的类型
     * @param includeNonSingletons 是否包含非单例的bean
     * @return 所有与指定类型相匹配的bean的名称
     */
    String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons);

    /**
     * 获取所有被指定注解标注的实例
     * @param annotationType 指定注解的类型
     * @return 所有被指定注解标注的实例的名称
     */
    String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

    <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons)
            throws BeansException;

    Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

    @Nullable
    <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
            throws NoSuchBeanDefinitionException;

}
