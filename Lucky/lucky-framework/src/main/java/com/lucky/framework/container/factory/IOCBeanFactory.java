package com.lucky.framework.container.factory;

import com.lucky.framework.container.FusionStrategy;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.RegisterMachine;
import com.lucky.framework.container.SingletonContainer;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.reflect.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午12:53
 */
public abstract class IOCBeanFactory implements BeanFactory,Namer {

    private FusionStrategy fusionStrategy;
    private SingletonContainer singletonPool= RegisterMachine.getRegisterMachine().getSingletonPool();
    private Set<Class<?>> plugins=RegisterMachine.getRegisterMachine().getPlugins();

    public IOCBeanFactory() {
    }

    public IOCBeanFactory(FusionStrategy fusionStrategy) {
        this.fusionStrategy = fusionStrategy;
    }

    public FusionStrategy getFusionStrategy() {
        return fusionStrategy;
    }

    public void setFusionStrategy(FusionStrategy fusionStrategy) {
        this.fusionStrategy = fusionStrategy;
    }

    public  void setSingletonPool(SingletonContainer singletonPool) {
        this.singletonPool=fusionStrategy.singletonPoolStrategy(this.singletonPool,singletonPool);
    }

    public void setPlugins(Set<Class<?>> plugins) {
        this.plugins=fusionStrategy.pluginsStrategy(this.plugins,plugins);
    }

    public List<Module> getBeanByType(String...types){
        return singletonPool.getBeanByType(types);
    }

    public List<Module> getBeanByClass(Class<?>...componentClasses){
        return singletonPool.getBeanByClass(componentClasses);
    }

    public List<Module> getBeanByAnnotation(Class<? extends Annotation>...annotationClasses){
        return singletonPool.getBeanByAnnotation(annotationClasses);
    }

    public Collection<Module> getBeans(){
        return singletonPool.values();
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

    public Set<Class<?>> getClasses(Class<?>...classes){
        return RegisterMachine.getRegisterMachine().getClasses(classes);
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

    @Override
    public double priority() {
        return 1;
    }
}
