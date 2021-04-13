package org.luckyframework.beans.factory;

import com.lucky.utils.base.Assert;
import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.*;
import org.luckyframework.beans.aware.BeanFactoryAware;
import org.luckyframework.beans.create.MightNeedBeanFactoryFactoryBean;
import org.luckyframework.beans.definition.BeanDefinition;
import org.luckyframework.beans.definition.DefaultBeanDefinitionRegistry;
import org.luckyframework.exception.BeanCreationException;
import org.luckyframework.exception.BeanCurrentlyInCreationException;
import org.luckyframework.exception.BeansException;
import org.luckyframework.exception.NoSuchBeanDefinitionException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 11:42
 */
public abstract class StandardBeanFactory extends DefaultBeanDefinitionRegistry implements ListableBeanFactory , BeanPostProcessorRegistry, InjectionRegistry {

    // 所有的BeanPostProcessor
    private final List<BeanPostProcessor> beanPostProcessors =new ArrayList<>(20);
    // 所有夫人属性注入组件
    private final List<Injection> injections = new ArrayList<>(10);
    //单例池
    private final Map<String,Object> singletonObjects = new ConcurrentHashMap<>(256);
    //实例化但未初始化的早期对象
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
    //正在创建的对象
    private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
    //缓存bean的类型信息
    private final Map<String,Class<?>> beanTypes = new ConcurrentHashMap<>(256);

    public StandardBeanFactory(){
        injections.add(new AutowiredInjection());
    }

    /**
     * 获取所有单例bean的名称
     * @return 所有单例bean的名称
     */
    public String[] getSingletonObjectNames(){
        return singletonObjects.keySet().toArray(EMPTY_STRING_ARRAY);
    }

    @Override
    public void registerBeanPostProcessor(BeanPostProcessor processor) {
        this.beanPostProcessors.add(processor);
    }

    @Override
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    @Override
    public void registerInjection(Injection injection) {
        this.injections.add(injection);
    }

    @Override
    public List<Injection> getInjections() {
        return this.injections;
    }

    @Override
    public void invokeAwareMethod(Object instance) {
        if(instance instanceof BeanFactoryAware){
            ((BeanFactoryAware)instance).setBeanFactory(this);
        }
    }

    // 获取Bean的实例
    protected Object doGetBean(String name){
        Object bean = singletonObjects.get(name);
        if(bean == null){
            BeanDefinition definition = getBeanDefinition(name);
            Assert.notNull(definition,"can not find the definition of bean '" + name +"'");
            bean = doCreateBean(name,definition);
        }
        if(NULL_OBJECT.equals(bean)){
            return null;
        }
        return bean;
    }

    // 创建Bean的实例
    private Object doCreateBean(String name, BeanDefinition definition) {
        Object instance = earlySingletonObjects.get(name);
        if(instance != null){
            return instance;
        }
        if(inCreationCheckExclusions.contains(name)){
            throw new BeanCurrentlyInCreationException("Error creating bean with name '"+name+"': Requested bean is currently in creation: Is there an unresolvable circular reference? '"+name+"' ↔ "+inCreationCheckExclusions);
        }
        inCreationCheckExclusions.add(name);
        instance = definition.getFactoryBean().getBean();
        if((instance instanceof MightNeedBeanFactoryFactoryBean) &&
            ((MightNeedBeanFactoryFactoryBean)instance).needBeanFactory()){
            ((MightNeedBeanFactoryFactoryBean)instance).setBeanFactory(this);
        }
        if(definition.isSingleton()){
            instance = getNotNullInstance(instance);
            earlySingletonObjects.put(name,instance);
        }
        applyInjectionProperties(instance);
        invokeAwareMethod(instance);
        instance=applyPostProcessBeforeInitialization(name,instance);
        doInit(name,instance);
        instance=applyPostProcessAfterInitialization(name,instance);
        inCreationCheckExclusions.remove(name);
        if(definition.isSingleton()){
            addSingletonObject(name,instance);
        }
        return instance;
    }

    // 初始化
    private void doInit(String beanName, Object instance) {
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        if(instance instanceof InitializingBean){
            try {
                ((InitializingBean)instance).afterPropertiesSet();
            } catch (Exception e) {
                throw new BeanCreationException("An exception occurred when using the 'InitializingBean#afterPropertiesSet()' method to initialize the bean named '"+beanName+"'",e);
            }
        }

        if(!Assert.isBlankString(beanDefinition.getInitMethodName())){
            try {
                Method initMethod = instance.getClass().getMethod(beanDefinition.getInitMethodName());
                initMethod.invoke(instance);
            }catch (Exception e){
                throw new BeanCreationException("An exception occurs when the bean named '"+beanName+"' is initialized using the initialization method the beanDefinition.  ["+beanDefinition+"]",e);
            }

        }
    }

    private void applyInjectionProperties(Object instance){
        for (Injection injection : injections) {
            injection.injection(instance,this);
        }
    }

    private Object applyPostProcessBeforeInitialization(String beanName,Object bean){
        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessBeforeInitialization(bean, beanName);
            if(bean == null){
                return null;
            }
        }
        return bean;
    }

    private Object applyPostProcessAfterInitialization(String beanName,Object bean){
        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessAfterInitialization(bean, beanName);
            if(bean == null){
                return null;
            }
        }
        return bean;
    }

    private Object getNotNullInstance(Object instance){
        return instance==null?NULL_OBJECT:instance;
    }

    // 添加单例到单例池
    public void addSingletonObject(String name,Object bean){
        bean = getNotNullInstance(bean);
        singletonObjects.put(name,bean);
        earlySingletonObjects.remove(name);
    }


    //---------------------------------------------------------------------
    // BeanFactory methods
    //---------------------------------------------------------------------
    @Override
    public Class<?> getType(String name) throws BeansException {
        BeanDefinition definition = getBeanDefinition(name);
        Assert.notNull(definition,"Cannot find the bean definition information with the name '"+name+"'");
        return definition.getFactoryBean().getBeanType().getRawClass();
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return this.doGetBean(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return (T) getBean(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return (T) getBean(ResolvableType.forType(requiredType));
    }

    @Override
    public Object getBean(ResolvableType requiredType) throws BeansException {
        return null;
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return null;
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return typeToMatch.isAssignableFrom(getType(name));
    }

    @Override
    public boolean containsBean(String name) {
        return containsBeanDefinition(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getBeanDefinition(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return getBeanDefinition(name).isPrototype();
    }

    @Override
    public void close() throws IOException {

    }
}
