package com.lucky.jacklamb.framework.container;

import com.lucky.jacklamb.framework.annotation.Configuration;
import com.lucky.jacklamb.framework.container.factory.BeanFactory;
import com.lucky.jacklamb.framework.container.factory.BeanNamer;
import com.lucky.jacklamb.framework.container.factory.ConfigurationBeanFactory;
import com.lucky.jacklamb.framework.container.factory.Namer;
import com.lucky.jacklamb.framework.uitls.reflect.AnnotationUtils;
import com.lucky.jacklamb.framework.uitls.reflect.ClassUtils;

import java.util.*;

/**
 * 注册机
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 1:30 下午
 */
public class RegisterMachine {

    private static RegisterMachine registerMachine;
    private SingletonContainer singletonPool;
    private Namer namer;
    private RegisterMachine(){
        singletonPool=new SingletonContainer();
        namer =new BeanNamer();
    }

    public static RegisterMachine getRegisterMachine(){
        if(registerMachine==null){
            registerMachine=new RegisterMachine();
        }
        return registerMachine;
    }

    public SingletonContainer getSingletonPool(){
        return this.singletonPool;
    }


    /**
     * 控制反转，将所有扫描得到的组件注册到IOC容器中
     * @param componentClasses IOC组件的Class集合
     */
    public void register(Set<Class<?>> componentClasses){
        Set<Class<?>> componentClassSet=new HashSet<>(150);
        Set<Class<? extends BeanFactory>> beanFactorySet=new HashSet<>(50);
        Set<Class<?>> configurationSet=new HashSet<>(50);
        for (Class<?> componentClass : componentClasses) {
            if(BeanFactory.class.isAssignableFrom(componentClass)){
                beanFactorySet.add((Class<? extends BeanFactory>) componentClass);
            }else if(AnnotationUtils.strengthenIsExist(componentClass, Configuration.class)){

            }else {
                componentClassSet.add(componentClass);
            }
        }

        //实例化所有的Bean，并将Bean实例注入到IOC容器中
        for (Class<?> component : componentClassSet) {
            Module module=new Module(namer.getBeanName(component),
                                     namer.getBeanType(component),
                                     ClassUtils.newObject(component));
            singletonPool.put(module.getId(),module);
        }

        //实例化配置类中的Bean方法实例化的Bean，并将其注入到IOC容器中
        new ConfigurationBeanFactory(configurationSet).createBean().stream().forEach(m->singletonPool.put(m.getId(),m));

        //实例化所有的BeanFactory，并将有BeanFactory创建出的Bean实例注入到IOC容器中
        for (Class<? extends BeanFactory> factoryClass : beanFactorySet) {
            BeanFactory beanFactory=ClassUtils.newObject(factoryClass);
            beanFactory.createBean().stream().forEach(m->singletonPool.put(m.getId(),m));
        }
    }

    public void injection(){

    }


}
