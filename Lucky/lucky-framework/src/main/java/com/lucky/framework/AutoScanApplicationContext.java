package com.lucky.framework;

import com.lucky.framework.container.Module;
import com.lucky.framework.container.RegisterMachine;
import com.lucky.framework.container.SingletonContainer;
import com.lucky.framework.scan.Scan;
import com.lucky.framework.scan.ScanFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/16 9:01
 */
public class AutoScanApplicationContext implements ApplicationContext{

    private static AutoScanApplicationContext autoScanApplicationContext;
    public Class<?> applicationBootClass;
    public SingletonContainer singletonPool;

    public static AutoScanApplicationContext create(){
        if(autoScanApplicationContext==null){
            autoScanApplicationContext=new AutoScanApplicationContext();
        }
        return autoScanApplicationContext;
    }

    public static AutoScanApplicationContext create(Class<?> applicationBootClass){
        if(autoScanApplicationContext==null){
            autoScanApplicationContext=new AutoScanApplicationContext(applicationBootClass);
        }
        return autoScanApplicationContext;
    }

    private AutoScanApplicationContext(){
        init();
    }

    private AutoScanApplicationContext(Class<?> applicationBootClass) {
        this.applicationBootClass = applicationBootClass;
        init();
    }

    private void init(){
        Scan scan= ScanFactory.createScan(applicationBootClass);
        RegisterMachine registerMachine=RegisterMachine.getRegisterMachine();
        registerMachine.setScan(scan);
        registerMachine.init();
        singletonPool=registerMachine.getSingletonPool();
    }

    @Override
    public Object getBean(String id) {
        return singletonPool.getBean(id).getComponent();
    }

    @Override
    public <T> List<T> getBean(Class<T> aClass) {
        List<Module> modules = singletonPool.getBeanByClass(aClass);
        List<T> list=new ArrayList<>(modules.size());
        modules.stream().forEach(m->list.add((T)m.getComponent()));
        return list;
    }

    @Override
    public List<Object> getBeanByAnnotation(Class<? extends Annotation> ann) {
        return singletonPool.getBeanByAnnotation(ann)
                .stream()
                .map(m->m.getComponent())
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> getBeans() {
        return singletonPool.values()
                .stream()
                .map(m->m.getComponent())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isIOCType(String type) {
        return singletonPool.containsType(type);
    }

    @Override
    public boolean isIOCId(String id) {
        return singletonPool.containsKey(id);
    }

    @Override
    public boolean isIOCClass(Class<?> componentClass) {
        return singletonPool.containsClass(componentClass);
    }
}
