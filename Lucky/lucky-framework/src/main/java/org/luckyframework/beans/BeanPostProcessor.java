package org.luckyframework.beans;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/16 0016 16:02
 */
public interface BeanPostProcessor {

    //bean初始化前增强
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    //bean初始化后增强
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
