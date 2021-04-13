package org.luckyframework.beans.definition;

import com.lucky.utils.reflect.ClassUtils;
import org.luckyframework.beans.ConstructorValue;
import org.luckyframework.beans.create.StaticFactoryMethodFactoryBean;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 11:12
 */
public class StaticFactoryMethodBeanDefinition extends GenericBeanDefinition {

    public StaticFactoryMethodBeanDefinition(Class<?> componentClass,String factoryMethodName){
        setFactoryBean(new StaticFactoryMethodFactoryBean(componentClass,factoryMethodName));
    }

    public StaticFactoryMethodBeanDefinition(Class<?> componentClass, String factoryMethodName, ConstructorValue[] constructorValues){
        setFactoryBean(new StaticFactoryMethodFactoryBean(componentClass,factoryMethodName,constructorValues));
    }

    public StaticFactoryMethodBeanDefinition(Class<?> componentClass, String factoryMethodName, Object[] realValues){
        setFactoryBean(new StaticFactoryMethodFactoryBean(componentClass,factoryMethodName,realValues));
    }

    public StaticFactoryMethodBeanDefinition(String componentClassStr,String factoryMethodName){
       this(ClassUtils.getClass(componentClassStr),factoryMethodName);
    }

    public StaticFactoryMethodBeanDefinition(String componentClassStr, String factoryMethodName, ConstructorValue[] constructorValues){
        this(ClassUtils.getClass(componentClassStr),factoryMethodName,constructorValues);
    }

    public StaticFactoryMethodBeanDefinition(String componentClassStr, String factoryMethodName, Object[] realValues){
        this(ClassUtils.getClass(componentClassStr),factoryMethodName,realValues);
    }
}
