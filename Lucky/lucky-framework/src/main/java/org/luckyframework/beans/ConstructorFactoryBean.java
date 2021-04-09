package org.luckyframework.beans;

import com.lucky.utils.reflect.ClassUtils;

import java.lang.reflect.Constructor;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/9 0009 11:57
 */
@SuppressWarnings("unchecked")
public class ConstructorFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> beanClass;
    private final ConstructorValue[] refArgs;

    public ConstructorFactoryBean(String beanClass){
        this((Class<T>)ClassUtils.getClass(beanClass));
    }

    public ConstructorFactoryBean(Class<T> beanClass) {
        this.beanClass = beanClass;
        this.refArgs = new ConstructorValue[0];
    }

    public ConstructorFactoryBean(Class<T> beanClass, ConstructorValue[] refArgs) {
        this.beanClass = beanClass;
        this.refArgs = refArgs;
    }

    @Override
    public T getBean() {
        Constructor<T> constructor = ClassUtils.findConstructor(beanClass, getRealArgsClasses());
        return ClassUtils.newObject(constructor,getRealArgs());
    }

    private Object getRealArgs() {
        return null;
    }

    private Class<?>[] getRealArgsClasses() {

        return null;
    }

    @Override
    public String toString() {
        return "new " + beanClass.getSimpleName() + "(" + refArgsToString() + ")";
    }

    private String refArgsToString(){
        if(refArgs == null){
            return null;
        }
        if(refArgs.length == 0){
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (ConstructorValue refArg : refArgs) {
            result.append(refArg.toString()).append(",");
        }
        return result.substring(0,result.length()-1);
    }

}
