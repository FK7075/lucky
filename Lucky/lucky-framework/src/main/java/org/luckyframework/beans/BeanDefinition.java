package org.luckyframework.beans;

import com.lucky.utils.type.ResolvableType;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/9 0009 11:48
 */
public interface BeanDefinition {

    /** 设置FactoryBean */
    void setFactoryBean(FactoryBean<?> factoryBean);

    /** 获取FactoryBean */
    FactoryBean<?> getFactoryBean();

    /** 设置Bean的作用域 */
    void setScope(BeanScope scope);

    /** 是否为单例 */
    boolean isSingleton();

    /** 是否为原型 */
    boolean isPrototype();

    /** 获取Bean的类型 */
    ResolvableType getBeanType();

}
