package org.luckyframework.beans.definition;

import com.lucky.utils.reflect.ClassUtils;
import org.luckyframework.beans.ConstructorValue;
import org.luckyframework.beans.create.ConstructorFactoryBean;
import org.luckyframework.beans.create.FactoryBean;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 11:03
 */
public class ConstructorBeanDefinition extends GenericBeanDefinition {

    public ConstructorBeanDefinition(Class<?> componentClass){
        setFactoryBean(new ConstructorFactoryBean(componentClass));
    }

    public ConstructorBeanDefinition(Class<?> componentClass, ConstructorValue[] constructorValues){
        setFactoryBean(new ConstructorFactoryBean(componentClass,constructorValues));
    }

    public ConstructorBeanDefinition(Class<?> componentClass, Object[] realValues){
        setFactoryBean(new ConstructorFactoryBean(componentClass,realValues));
    }

    public ConstructorBeanDefinition(String componentClassStr){
        this(ClassUtils.getClass(componentClassStr));
    }

    public ConstructorBeanDefinition(String componentClassStr, ConstructorValue[] constructorValues){
        this(ClassUtils.getClass(componentClassStr),constructorValues);
    }

    public ConstructorBeanDefinition(String componentClassStr, Object[] realValues){
        this(ClassUtils.getClass(componentClassStr),realValues);
    }
}
