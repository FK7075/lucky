package com.lucky.jacklamb.framework.container;

import com.lucky.jacklamb.framework.container.factory.BeanFactory;
import com.lucky.jacklamb.framework.container.factory.BeanNamer;
import com.lucky.jacklamb.framework.container.factory.ConfigurationBeanFactory;
import com.lucky.jacklamb.framework.container.factory.Namer;
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
        //实例化所有扫描到的Bean实例，并注入到IOC容器中
        for (Class<?> componentClass : componentClasses) {
            Module module=new Module(namer.getBeanName(componentClass)
                                    ,namer.getBeanType(componentClass)
                                    ,ClassUtils.newObject(componentClass));
            singletonPool.put(module.getId(),module);
        }

        //找到IOC容器中所有的配置类，初始化所有配置类生产的Bean实例，并注入IOC容器
        ConfigurationBeanFactory configurationBeanFactory=
                new ConfigurationBeanFactory(singletonPool.getBeanByType("configuration"));
        configurationBeanFactory.createBean().stream().forEach(m->singletonPool.put(m.getId(),m));

        //找到IOC容器中所有的BeanFactory，并将这些BeanFactory生产的Bean实例注入IOC容器
        singletonPool.getBeanByClass(BeanFactory.class).stream().forEach(beanFactoryModule->{
            BeanFactory beanFactory = (BeanFactory) beanFactoryModule.getComponent();
            beanFactory.createBean().stream().forEach(m->singletonPool.put(m.getId(),m));
        });

    }

    public void injection(){
        singletonPool.values().stream().forEach(module -> {
            Injection.injection(module.getComponent());
        });
    }


}
