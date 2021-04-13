package org.luckyframework.beans.create;

import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.MethodUtils;
import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.Autowire;
import org.luckyframework.beans.BeanReference;
import org.luckyframework.beans.ConstructorValue;
import org.luckyframework.beans.factory.BeanFactory;

import java.lang.reflect.Method;

/**
 * 基于工厂方法的FactoryBean
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 9:39
 */

public class FactoryMethodFactoryBean extends AbstractFactoryBean {

    private Object factory;
    private String methodName;

    private void init(Object factory,String methodName){
        if(
           (factory instanceof BeanReference)||
           ((factory instanceof MightNeedBeanFactoryFactoryBean) && ((MightNeedBeanFactoryFactoryBean)factory).needBeanFactory())){
            needBeanFactory = true;
        }
        this.factory = factory;
        this.methodName = methodName;
    }

    public FactoryMethodFactoryBean(Object factory,String methodName){
        super();
        init(factory, methodName);
    }

    public FactoryMethodFactoryBean(Object factory,String methodName,ConstructorValue[] constructorValues){
        super(constructorValues);
        init(factory, methodName);
    }

    public FactoryMethodFactoryBean(Object factory,String methodName,Object[] realValues){
        super(realValues);
        init(factory, methodName);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        if(factory instanceof MightNeedBeanFactoryFactoryBean){
            MightNeedBeanFactoryFactoryBean factoryBean = (MightNeedBeanFactoryFactoryBean) factory;
            if(factoryBean.needBeanFactory()){
                factoryBean.setBeanFactory(beanFactory);
            }
        }
    }

    @Override
    public Object getBean() {
        Object factoryObject = getObjectByBeanFactory(factory);
        Method method = ClassUtils.findMethod(factoryObject.getClass(), methodName, getRealArgsClasses());
        return MethodUtils.invoke(factoryObject,method,getRealArgs());
    }

    @Override
    public ResolvableType getBeanType() {
        Object factoryObject = getObjectByBeanFactory(factory);
        Method method = ClassUtils.findMethod(factoryObject.getClass(), methodName, getRealArgsClasses());
        return ResolvableType.forMethodReturnType(method);
    }

    @Override
    public String toString(){
        if(factory instanceof BeanReference){
            BeanReference ref = (BeanReference) factory;
            if(ref.getAutowire() == Autowire.BY_NAME){
                return "REF<"+ref.getBeanName()+">."+methodName+argsToString();
            }else{
                return "REF<"+ref.getReferenceType(beanFactory)+">."+methodName+argsToString();
            }
        }else{
            return "Obj<"+ BaseUtils.lowercaseFirstLetter(factory.getClass().getSimpleName()) +">."+methodName+argsToString();
        }
    }

}
