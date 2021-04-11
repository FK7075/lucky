package org.luckyframework.beans;

import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.MethodUtils;

import java.lang.reflect.Method;

/**
 * 基于静态工厂方法的FactoryBean
 * @author fk7075
 * @version 1.0.0
 * @date 2021/4/12 上午12:51
 */
@SuppressWarnings("unchecked")
public class StaticFactoryMethodFactoryBean<T> extends AbstractFactoryBean<T>{

    private final Class<?> factoryClass;
    private final String methodName;

    public StaticFactoryMethodFactoryBean(Class<?> factoryClass,String methodName){
        super();
        this.factoryClass = factoryClass;
        this.methodName = methodName;
    }

    public StaticFactoryMethodFactoryBean(String factoryClass,String methodName){
        this(ClassUtils.getClass(factoryClass),methodName);
    }

    public StaticFactoryMethodFactoryBean(Class<?> factoryClass,String methodName,ConstructorValue[] refValues){
        super(refValues);
        this.factoryClass = factoryClass;
        this.methodName = methodName;
    }

    public StaticFactoryMethodFactoryBean(Class<?> factoryClass,String methodName,Object[] realValues){
        super(realValues);
        this.factoryClass = factoryClass;
        this.methodName = methodName;
    }

    @Override
    public T getBean() {
        Method method = ClassUtils.findMethod(factoryClass, methodName, getRealArgsClasses());
        return (T) MethodUtils.invoke(factoryClass,method,getRealArgs());
    }

    @Override
    public String toString(){
        return factoryClass.getSimpleName()+"."+methodName+argsToString();
    }

}
