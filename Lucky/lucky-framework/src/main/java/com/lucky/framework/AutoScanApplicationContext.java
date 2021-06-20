package com.lucky.framework;

import com.lucky.framework.container.Module;
import com.lucky.framework.container.RegisterMachine;
import com.lucky.framework.container.SingletonContainer;
import com.lucky.framework.container.factory.Destroy;
import com.lucky.framework.scan.JarExpandChecklist;
import com.lucky.framework.scan.Scan;
import com.lucky.framework.scan.ScanFactory;
import com.lucky.framework.welcome.JackLamb;
import com.lucky.utils.type.ResolvableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/16 9:01
 */
public class AutoScanApplicationContext implements ApplicationContext{

    private static final Logger log= LoggerFactory.getLogger("c.l.framework.AutoScanApplicationContext");

    private static AutoScanApplicationContext autoScanApplicationContext;
    private static final RuntimeMXBean mxb = ManagementFactory.getRuntimeMXBean();
    public static boolean isInit=false;
    public Class<?> applicationBootClass;
    public SingletonContainer singletonPool;
    private RegisterMachine registerMachine;

    static {
        String pid = mxb.getName().split("@")[0];
        MDC.put("pid",pid);
    }

    public Class<?> getApplicationBootClass() {
        return applicationBootClass;
    }

    public static AutoScanApplicationContext create(){
        return create(false);
    }

    public static AutoScanApplicationContext create(Class<?> applicationBootClass){
        return create(applicationBootClass,false);
    }

    public static AutoScanApplicationContext create(boolean adverbRefresh){
        if(adverbRefresh){
            if(autoScanApplicationContext != null){
                autoScanApplicationContext.destroy();
                autoScanApplicationContext = null;
            }
        }
        if(autoScanApplicationContext==null){
            autoScanApplicationContext=new AutoScanApplicationContext();
        }
        return autoScanApplicationContext;
    }


    public static AutoScanApplicationContext create(Class<?> applicationBootClass,boolean adverbRefresh){
        if(adverbRefresh){
            if(autoScanApplicationContext != null){
                autoScanApplicationContext.destroy();
                autoScanApplicationContext = null;
            }
        }
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
        JackLamb.welcome();
        log.info("IOC container starts to initialize！ROOT-CLASS => `{}`",applicationBootClass);
        Scan scan= ScanFactory.createScan(applicationBootClass);
        registerMachine=RegisterMachine.getRegisterMachine(true);
        registerMachine.setScan(scan);
        registerMachine.init();
        singletonPool=registerMachine.getSingletonPool();
        isInit=true;
    }

    private void destroy(){
        applicationBootClass = null;
        registerMachine = null;
        singletonPool = null;
        isInit = false;
        autoScanApplicationContext = null;
    }

    @Override
    public Object getBean(String id) {
        return singletonPool.getBean(id).getComponent();
    }

    @Override
    public Module getModule(String id) {
        return singletonPool.getBean(id);
    }

    @Override
    public <T> List<T> getBean(Class<T>...aClasses) {
        List<Module> modules = singletonPool.getBeanByClass(aClasses);
        List<T> list=new ArrayList<>(modules.size());
        modules.forEach(m->list.add((T)m.getComponent()));
        return list;
    }

    @Override
    public List<Module> getModule(Class<?>...aClasses) {
        return singletonPool.getBeanByClass(aClasses);
    }

    @Override
    public List<?> getBeanByAnnotation(Class<? extends Annotation>...annotationClasses) {
        return singletonPool.getBeanByAnnotation(annotationClasses)
                .stream()
                .map(Module::getComponent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Module> getModuleByAnnotation(Class<? extends Annotation>...annotationClasses){
        return singletonPool.getBeanByAnnotation(annotationClasses);
    }

    @Override
    public List<?> getBeans() {
        return singletonPool.values()
                .stream()
                .map(Module::getComponent)
                .collect(Collectors.toList());
    }

    @Override
    public List<Module> getModules() {
        return (List<Module>) singletonPool.values();
    }

    @Override
    public List<?> getBeanByType(String...iocType) {
        return singletonPool.getBeanByType(iocType)
                .stream()
                .map(m->m.getComponent())
                .collect(Collectors.toList());
    }

    @Override
    public List<Module> getModuleByType(String...iocType) {
        return singletonPool.getBeanByType(iocType);
    }

    public SingletonContainer getNewSingletonPool(JarExpandChecklist jarExpandChecklist) throws IOException {
        return RegisterMachine.dynamicAssembly(jarExpandChecklist);
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

    @Override
    public void put(Module module) {
        singletonPool.put(module.getId(),module);
    }

    @Override
    public Set<Class<?>> getClasses(Class<?>... aClasses) {
        return registerMachine.getClasses(aClasses);
    }

    @Override
    public Module getModuleByField(Class<?> beanClass, Class<?> autowiredClass) {
        return singletonPool.getBeanByField(beanClass, autowiredClass);
    }

    @Override
    public Object getBeanByField(Class<?> beanClass, Class<?> autowiredClass) {
        Module module = getModuleByField(beanClass, autowiredClass);
        return module==null?null:module.getComponent();
    }

    @Override
    public void close() throws IOException {
        getBean(Destroy.class).stream().forEach(d-> {
            try {
                d.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public Module getModuleByResolvableType(ResolvableType resolvableType) {
        return singletonPool.getModuleByResolvableType(resolvableType);
    }

    @Override
    public Object getBeanByResolvableType(ResolvableType resolvableType) {
        return singletonPool.getBeanByResolvableType(resolvableType);
    }
}
