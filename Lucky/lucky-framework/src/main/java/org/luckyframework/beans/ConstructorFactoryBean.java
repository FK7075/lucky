package org.luckyframework.beans;

import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.ClassUtils;
import org.luckyframework.beans.factory.BeanFactory;

import java.lang.reflect.Constructor;

/**
 * 基于构造器的工厂Bean
 * @author fk
 * @version 1.0
 * @date 2021/4/9 0009 11:57
 */
@SuppressWarnings("unchecked")
public class ConstructorFactoryBean<T> extends AbstractFactoryBean<T> {

    private final Class<T> beanClass;

    public ConstructorFactoryBean(String beanClass){
        this((Class<T>)ClassUtils.getClass(beanClass));
    }

    public ConstructorFactoryBean(Class<T> beanClass) {
        super();
        this.beanClass = beanClass;
    }

    public ConstructorFactoryBean(Class<T> beanClass, ConstructorValue[] refArgs) {
        super(refArgs);
        this.beanClass = beanClass;
    }

    public ConstructorFactoryBean(Class<T> beanClass,Object[] realValues){
        super(realValues);
        this.beanClass = beanClass;
    }

    @Override
    public T getBean() {
        Constructor<T> constructor = ClassUtils.findConstructor(beanClass, getRealArgsClasses());
        return ClassUtils.newObject(constructor,getRealArgs());
    }

    @Override
    public String toString() {
        return "new " + beanClass.getSimpleName() + "(" + argsToString() + ")";
    }

}
