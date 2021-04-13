package org.luckyframework.beans.definition;

import org.luckyframework.beans.ConstructorValue;
import org.luckyframework.beans.create.FactoryMethodFactoryBean;

/**
 * 基于工厂方法的Bean定义信息
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 11:17
 */
public class FactoryMethodBeanDefinition extends GenericBeanDefinition {

    public FactoryMethodBeanDefinition(Object factoryBean,String methodName){
        setFactoryBean(new FactoryMethodFactoryBean(factoryBean,methodName));
    }

    public FactoryMethodBeanDefinition(Object factoryBean, String methodName, ConstructorValue[] constructorValues){
        setFactoryBean(new FactoryMethodFactoryBean(factoryBean,methodName,constructorValues));
    }

    public FactoryMethodBeanDefinition(Object factoryBean, String methodName, Object[] realValues){
        setFactoryBean(new FactoryMethodFactoryBean(factoryBean,methodName,realValues));
    }

}
