package com.lucky.framework.container;

import com.lucky.framework.annotation.Plugin;
import com.lucky.framework.container.factory.*;
import com.lucky.framework.scan.Scan;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * 注册机
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 1:30 下午
 */
public class RegisterMachine {

    private static final Logger log= LogManager.getLogger("c.l.framework.container.RegisterMachine");

    private static RegisterMachine registerMachine;
    private SingletonContainer singletonPool;
    private Set<Class<?>> plugins;
    private static Namer namer=new BeanNamer();
    private Scan scan;
    private RegisterMachine(){
        singletonPool=new SingletonContainer();
        plugins=new HashSet<>(20);
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public void init() {
        register();
        injection();
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

    public Set<Class<?>> getPlugins() {
        return plugins;
    }

    /**
     * 控制反转，将所有扫描得到的组件注册到IOC容器中
     */
    public void register(){
        Set<Class<?>> componentClasses=scan.getComponentClass();
        //实例化所有扫描到的Bean实例，并注入到IOC容器中
        for (Class<?> componentClass : componentClasses) {
            //将所有插件Class过滤到插件集合中
            if(AnnotationUtils.strengthenIsExist(componentClass, Plugin.class)){
                plugins.add(componentClass);
                log.debug("Plugin [class=`"+componentClass+"`]");
                continue;
            }

            //实例化所有的Bean，并注入到IOC容器
            Module module=new Module(namer.getBeanName(componentClass)
                                    ,namer.getBeanType(componentClass)
                                    , ClassUtils.newObject(componentClass));
            singletonPool.put(module.getId(),module);
            log.info("Component `{}`",module);
        }

        //找到IOC容器中所有的配置类，初始化所有配置类生产的Bean实例，并注入IOC容器
        ConfigurationBeanFactory configurationBeanFactory=
                new ConfigurationBeanFactory(singletonPool.getBeanByType("configuration"));
        configurationBeanFactory.createBean().stream().forEach((m)->{
            singletonPool.put(m.getId(),m);
            log.info("Configuration Bean `{}`",m);
        });

        //找到IOC容器中所有的BeanFactory，并将这些BeanFactory生产的Bean实例注入IOC容器
        singletonPool.getBeanByClass(BeanFactory.class).stream().sorted(Comparator.comparing((m)->{
            BeanFactory beanFactory=(BeanFactory)m.getComponent();
            return beanFactory.priority();
        })).forEach(beanFactoryModule->{
            BeanFactory beanFactory = (BeanFactory) beanFactoryModule.getComponent();
            log.info("BeanFactory `{}`",beanFactory);
            beanFactory.createBean().stream().forEach((m)->{
                singletonPool.put(m.getId(),m);
                log.info("Factory Create Bean `{}`",m);
            });
            Map<String, Module> replaceBeans = beanFactory.replaceBean();
            replaceBeans.keySet().stream().forEach((k)->{
                Module newModule = replaceBeans.get(k);
                singletonPool.replace(k,newModule);
                log.info("Replace Bean To `{}`",newModule);
            });
        });
    }

    /**
     * 动态组装，在运行期间动态组装一些Bean的实例到IOC容器中
     * @param componentClasses 需要动态加载的组件
     */
    public static SingletonContainer dynamicAssembly(Set<IOCBeanFactory> beanFactorys, Set<Class<?>> componentClasses){
        //动态插件库
        Set<Class<?>> plugins=new HashSet<>(20);
        //动态容器
        SingletonContainer singletonPool=new SingletonContainer();
        for (Class<?> componentClass : componentClasses) {
            //将所有插件Class过滤到插件集合中
            if(AnnotationUtils.strengthenIsExist(componentClass, Plugin.class)){
                plugins.add(componentClass);
                log.debug("Plugin `{}`",componentClass);
                continue;
            }

            //实例化所有的Bean，并注入到IOC容器
            Module module=new Module(namer.getBeanName(componentClass)
                    ,namer.getBeanType(componentClass)
                    , ClassUtils.newObject(componentClass));
            singletonPool.put(module.getId(),module);
            log.info("Component `{}`",module);
        }

        //找到IOC容器中所有的配置类，初始化所有配置类生产的Bean实例，并注入IOC容器
        ConfigurationBeanFactory configurationBeanFactory=
                new ConfigurationBeanFactory(singletonPool.getBeanByType("configuration"));
        configurationBeanFactory.createBean().stream().forEach((m)->{
            singletonPool.put(m.getId(),m);
            log.info("Configuration Bean `{}`",m);
        });

        //找到IOC容器中所有的BeanFactory，并将这些BeanFactory生产的Bean实例注入IOC容器
        beanFactorys.stream().sorted(Comparator.comparing(m-> m.priority())).forEach(beanFactory->{
            beanFactory.setSingletonPool(singletonPool);
            beanFactory.setPlugins(plugins);
            beanFactory.createBean().stream().forEach((m)->{
                singletonPool.put(m.getId(),m);
                log.info("Factory Create Bean `{}`",m);
            });
            Map<String, Module> replaceBeans = beanFactory.replaceBean();
            replaceBeans.keySet().stream().forEach((k)->{
                Module newModule = replaceBeans.get(k);
                singletonPool.replace((k),replaceBeans.get(k));
                log.info("Replace Bean To `{}`",newModule);
            });
        });

        singletonPool.values().stream().forEach(module -> {
            Injection.setSingletonPool(singletonPool);
            Injection.injection(module);
        });
       return singletonPool;
    }

    public void injection(){
        singletonPool.values().stream().forEach(module -> {
            Injection.injection(module);
        });
    }


}