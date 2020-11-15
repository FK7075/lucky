package com.lucky.jacklamb.framework.container.factory;

import com.lucky.jacklamb.framework.container.Module;
import com.lucky.jacklamb.framework.container.RegisterMachine;
import com.lucky.jacklamb.framework.container.SingletonContainer;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午12:53
 */
public abstract class IOCBeanFactory implements BeanFactory {

    private static final SingletonContainer singletonPool= RegisterMachine.getRegisterMachine().getSingletonPool();

    public List<Module> getBeanByType(String type){
        return singletonPool.getBeanByType(type);
    }

    public List<Module> getBeanByClass(Class<?> componentClass){
        return singletonPool.getBeanByClass(componentClass);
    }

    public List<Module> getBeanByAnnotation(Class<? extends Annotation> annotationClass){
        return singletonPool.getBeanByAnnotation(annotationClass);
    }

    public Module getBean(String id){
        return singletonPool.getBean(id);
    }

}
