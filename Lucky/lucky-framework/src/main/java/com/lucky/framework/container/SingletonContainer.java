package com.lucky.framework.container;

import com.lucky.framework.exception.LuckyBeanCreateException;
import com.lucky.utils.annotation.NonNull;
import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.type.ResolvableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 1:12 下午
 */
public class SingletonContainer implements Map<String, Module> {

    private static final Logger log= LoggerFactory.getLogger("c.l.f.container.SingletonContainer");

    /** 单例池*/
    private final Map<String,Module> singletonPool=new ConcurrentHashMap<>(256);

    public Map<String, Module> getSingletonPool() {
        return singletonPool;
    }

    @Override
    public int size() {
        return singletonPool.size();
    }

    @Override
    public boolean isEmpty() {
        return singletonPool.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return singletonPool.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return singletonPool.containsValue(value);
    }

    @Override
    public Module get(Object key) {
        return singletonPool.get(key);
    }

    @Override
    public Module put(String key, Module value) {
        if(containsKey(key)){
            LuckyBeanCreateException lex = new LuckyBeanCreateException("ID为 `" + key + "` 的组件已经存在，无法重复创建！  具体信息："+value);
            log.error("LuckyBeanCreateException",lex);
            throw lex;
        }
        return singletonPool.put(key, value);
    }

    public Module put(Module module){
        return put(module.getId(),module);
    }

    @Override
    public Module replace(String key, Module value){
        if(containsKey(key)){
            remove(key);
        }
        put(key,value);
        return value;
    }

    public Module replace(Module module){
        return replace(module.getId(),module);
    }

    @Override
    public Module remove(Object key) {
        return singletonPool.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Module> m) {
        for(Map.Entry<? extends String,?extends Module> entry:m.entrySet()){
            put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void clear() {
        singletonPool.clear();
    }

    @Override
    @NonNull
    public Set<String> keySet() {
        return singletonPool.keySet();
    }

    @Override
    @NonNull
    public Collection<Module> values() {
        return singletonPool.values();
    }

    @Override
    @NonNull
    public Set<Entry<String, Module>> entrySet() {
        return singletonPool.entrySet();
    }

    @Override
    @NonNull
    public boolean equals(Object o) {
        return singletonPool.equals(o);
    }

    @Override
    public int hashCode() {
        return singletonPool.hashCode();
    }

    public List<Module> getBeanByType(String...types){
        return singletonPool.values()
                .stream()
                .filter((m)->{
                    for (String type : types) {
                        if(m.getType().equals(type)){
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<Module> getBeanByClass(Class<?>...componentClasses){
        return singletonPool.values()
                .stream()
                .filter((m)->{
                    for (Class<?> componentClass : componentClasses) {
                        if(componentClass.isAssignableFrom(m.getOriginalType())){
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<Module> getModulesByResolvableType(ResolvableType resolvableType){
        List<Module> list = new ArrayList<>();
        for (Entry<String, Module> entry : singletonPool.entrySet()) {
            Module module = entry.getValue();
            if (resolvableType.isInstance(module.getComponent())) {
                list.add(module);
                continue;
            }
            ResolvableType beanResolvableType = module.getResolvableType();
            if(resolvableType.hasGenerics()){
                if(resolvableType.toString().equals(beanResolvableType.toString())){
                    list.add(module);
                }
            }else{
                if(resolvableType.resolve().isAssignableFrom(beanResolvableType.resolve())){
                    list.add(module);
                }
            }
        }
        return list;
    }

    public Module getModuleByResolvableType(ResolvableType resolvableType){
        List<Module> modules = getModulesByResolvableType(resolvableType);
        return Assert.isEmptyCollection(modules)?null:modules.get(0);
    }

    public Object getBeanByResolvableType(ResolvableType resolvableType){
        Module module = getModuleByResolvableType(resolvableType);
        return module == null?null:module.getComponent();
    }



    public Module getBeanByField(Class<?> beanClass, Class<?> autowiredClass){
        List<Module> modules = getBeanByClass(autowiredClass);
        if(Assert.isEmptyCollection(modules)){
            return null;
        }
        if(modules.size()==1){
            return modules.get(0);
        }
        Class<?>[] genericTypes = ClassUtils.getGenericType(beanClass.getGenericSuperclass());
        if(!Assert.isEmptyArray(genericTypes)){
            Class<?> genericType=null;
            List<Module> filterBeans=new ArrayList<>();
            for (Class<?> type : genericTypes) {
                if(autowiredClass.isAssignableFrom(type)){
                    genericType=type;
                    continue;
                }
            }
            for (Module module : modules) {
                assert genericType != null;
                if(genericType.isAssignableFrom(module.getOriginalType())){
                    filterBeans.add(module);
                }
            }

            if(filterBeans.size()==1){
                return filterBeans.get(0);
            }
        }
        return null;

    }

    public List<Module> getBeanByAnnotation(Class<? extends Annotation>...annotationClasses){
        return singletonPool.values()
                .stream()
                .filter((m)-> {
                    for (Class<? extends Annotation> annotationClass : annotationClasses) {
                        if(AnnotationUtils.isExist(m.getOriginalType(),annotationClass)){
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public Module getBean(String id){
        return singletonPool.get(id);
    }

    public boolean containsType(String type){
        Collection<Module> values = values();
        for (Module value : values) {
            if(value.getType().equals(type)){
                return true;
            }
        }
        return false;
    }

    public boolean containsClass(Class<?> beanClass){
        Collection<Module> values = values();
        for (Module value : values) {
            if(beanClass.isAssignableFrom(value.getOriginalType())){
                return true;
            }
        }
        return false;
    }


}
