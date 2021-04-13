package org.luckyframework.beans.create;

import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.MethodUtils;
import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.ConstructorValue;

import java.lang.reflect.Method;

/**
 * 基于静态工厂方法的FactoryBean
 * @author fk7075
 * @version 1.0.0
 * @date 2021/4/12 上午12:51
 */
public class StaticFactoryMethodFactoryBean extends AbstractFactoryBean {

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

    public StaticFactoryMethodFactoryBean(Class<?> factoryClass, String methodName, ConstructorValue[] refValues){
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
    public Object getBean() {
        Method method = ClassUtils.findMethod(factoryClass, methodName, getRealArgsClasses());
        return MethodUtils.invoke(factoryClass,method,getRealArgs());
    }

    @Override
    public ResolvableType getBeanType() {
        Method method = ClassUtils.findMethod(factoryClass, methodName, getRealArgsClasses());
        return ResolvableType.forMethodReturnType(method);
    }

    @Override
    public String toString(){
        return factoryClass.getSimpleName()+"."+methodName+argsToString();
    }

}
