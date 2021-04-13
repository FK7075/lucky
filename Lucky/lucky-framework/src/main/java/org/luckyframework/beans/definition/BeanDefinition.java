package org.luckyframework.beans.definition;

import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.BeanScope;
import org.luckyframework.beans.create.FactoryBean;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/9 0009 11:48
 */
public interface BeanDefinition {

    /** 设置FactoryBean */
    void setFactoryBean(FactoryBean factoryBean);

    /** 获取FactoryBean */
    FactoryBean getFactoryBean();

    /** 设置Bean的作用域 */
    void setScope(BeanScope scope);

    /** 是否为单例 */
    boolean isSingleton();

    /** 是否为原型 */
    boolean isPrototype();

    /** 是否延迟初始化 */
    boolean isLazyInit();

    /** 设置是否延迟初始化 */
    void setLazyInit(boolean lazyInit);

    /** 设置初始化方法 */
    void setInitMethodName(String initMethodName);

    /** 获取初始化方法 */
    String getInitMethodName();

    /** 设置销毁方法 */
    void setDestroyMethodName(String destroyMethodName);

    /** 获取销毁方法 */
    String getDestroyMethodName();

    /** 复制*/
    BeanDefinition copy();

    /** 获取Bean的类型 */
    ResolvableType getBeanType();

}
