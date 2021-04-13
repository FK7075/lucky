package org.luckyframework.beans.create;

import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.ConstructorValue;

import java.lang.reflect.Constructor;

/**
 * 基于构造器的工厂Bean
 * @author fk
 * @version 1.0
 * @date 2021/4/9 0009 11:57
 */
public class ConstructorFactoryBean extends AbstractFactoryBean {

    private final Class<?> beanClass;

    public ConstructorFactoryBean(String beanClass){
        this(ClassUtils.getClass(beanClass));
    }

    public ConstructorFactoryBean(Class<?> beanClass) {
        super();
        this.beanClass = beanClass;
    }

    public ConstructorFactoryBean(Class<?> beanClass, ConstructorValue[] refArgs) {
        super(refArgs);
        this.beanClass = beanClass;
    }

    public ConstructorFactoryBean(Class<?> beanClass,Object[] realValues){
        super(realValues);
        this.beanClass = beanClass;
    }

    @Override
    public Object getBean() {
        Constructor<?> constructor = ClassUtils.findConstructor(beanClass, getRealArgsClasses());
        return ClassUtils.newObject(constructor,getRealArgs());
    }

    @Override
    public ResolvableType getBeanType() {
        return ResolvableType.forType(beanClass);
    }

    @Override
    public String toString() {
        return "new " + beanClass.getSimpleName() + argsToString();
    }

}
