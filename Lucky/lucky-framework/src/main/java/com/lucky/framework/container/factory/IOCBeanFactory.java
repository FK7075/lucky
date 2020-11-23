package com.lucky.framework.container.factory;

import com.lucky.framework.container.Module;
import com.lucky.framework.container.RegisterMachine;
import com.lucky.framework.container.SingletonContainer;
import com.lucky.framework.uitls.base.BaseUtils;
import com.lucky.framework.uitls.reflect.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午12:53
 */
public abstract class IOCBeanFactory implements BeanFactory,Namer {

    private static final SingletonContainer singletonPool= RegisterMachine.getRegisterMachine().getSingletonPool();
    private static final Set<Class<?>> plugins=RegisterMachine.getRegisterMachine().getPlugins();

    public List<Module> getBeanByType(String...types){
        return singletonPool.getBeanByType(types);
    }

    public List<Module> getBeanByClass(Class<?>...componentClasses){
        return singletonPool.getBeanByClass(componentClasses);
    }

    public List<Module> getBeanByAnnotation(Class<? extends Annotation>...annotationClasses){
        return singletonPool.getBeanByAnnotation(annotationClasses);
    }

    public boolean isIOCType(String type) {
        return singletonPool.containsType(type);
    }

    public boolean isIOCId(String id) {
        return singletonPool.containsKey(id);
    }

    public boolean isIOCClass(Class<?> componentClass) {
        return singletonPool.containsClass(componentClass);
    }

    public Module getBean(String id){
        return singletonPool.getBean(id);
    }

    public List<Class<?>> getPluginByClass(Class<?>...pluginClasses){
        return plugins.stream()
                .filter((pc)->{
                    for (Class<?> pluginClass : pluginClasses) {
                        if(pluginClass.isAssignableFrom(pc)){
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<Class<?>> getPluginByAnnotation(Class<? extends Annotation>...annotationClasses){
        return plugins.stream()
                .filter((a)->{
                    for (Class<? extends Annotation> annotationClass : annotationClasses) {
                        if(AnnotationUtils.isExist(a,annotationClass)){
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<Class<?>> getPlugins(){
        return new ArrayList<>(plugins);
    }

    @Override
    public String getBeanName(Class<?> aClass){
        return BaseUtils.lowercaseFirstLetter(aClass.getSimpleName());
    }

    @Override
    public String getBeanType(Class<?> aClass){
        return "component";
    }

    @Override
    public Map<String, Module> replaceBean() {
        return new HashMap<>();
    }

    @Override
    public List<Module> createBean() {
        return new ArrayList<>();
    }
}
