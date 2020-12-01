package com.lucky.aop.beanfactory;

import com.lucky.aop.annotation.Aspect;
import com.lucky.aop.annotation.Expand;
import com.lucky.aop.core.AopPoint;
import com.lucky.aop.core.AopProxyFactory;
import com.lucky.aop.core.InjectionAopPoint;
import com.lucky.aop.core.PointRun;
import com.lucky.framework.FusionStrategy;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.AopBeanFactory;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.base.BaseUtils;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 创建Lucky AOP代理对象的BeanFactory
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/28 上午4:14
 */
public class LuckyAopBeanFactory extends AopBeanFactory {

    private static final Logger log= LogManager.getLogger("c.l.aop.beanfactory.LuckyAopBeanFactory");
    private Set<PointRun> pointRunSet;
    private static final String PROXY_NAME="$$LUCKY_PROXY$$";
    public LuckyAopBeanFactory(){
        super();
        pointRunSet=new HashSet<>(30);
    }

    public LuckyAopBeanFactory(FusionStrategy fusionStrategy){
        super(fusionStrategy);
        pointRunSet=new HashSet<>(30);
    }


    @Override
    public List<Module> createBean() {
        List<Module> pointModules=new ArrayList<>(30);
        List<Class<?>> aspectPluginClasses = getPluginByAnnotation(Aspect.class)
                .stream()
                .filter(c->!AopPoint.class.isAssignableFrom(c))
                .collect(Collectors.toList());
        for (Class<?> aspectPluginClass : aspectPluginClasses) {
            Object aspectObject = ClassUtils.newObject(aspectPluginClass);
            if(!isIOCId(getBeanName(aspectPluginClass))){
                pointModules.add(new Module(getBeanName(aspectPluginClass),"aspect",aspectObject));
            }
            List<Method> expandMethods = ClassUtils.getMethodByStrengthenAnnotation(aspectPluginClass, Expand.class);
            for (Method expandMethod : expandMethods) {
                pointRunSet.add(new PointRun(aspectObject,expandMethod));
            }
        }
        List<Module> aopPointModuleList = getBeanByClass(AopPoint.class);
        Set<InjectionAopPoint> injectionAopPoints=new HashSet<>(10);
        for (Module aopPointModule : aopPointModuleList) {
            Object apoPoint = aopPointModule.getComponent();
            if(InjectionAopPoint.class.isAssignableFrom(apoPoint.getClass())){
                injectionAopPoints.add((InjectionAopPoint)apoPoint);
            }else{
                pointRunSet.add(new PointRun((AopPoint)apoPoint));
            }
        }

        AopProxyFactory.injectionAopPointSet=injectionAopPoints;
        Collection<Module> beans = getBeans();
        for (Module bean : beans) {
            Class<?> originalType = bean.getOriginalType();
            if(originalType.getName().contains(PROXY_NAME)
                    ||bean.getComponent().getClass().getName().contains(PROXY_NAME)){
                continue;
            }
            Object aspect = AopProxyFactory.aspect(pointRunSet, bean);
            if(aspect.getClass().getName().contains(PROXY_NAME)){
                bean.setComponent(aspect);
                log.info("Create Aop Proxy Bean `{}`",bean.getComponent());
            }
        }
        return pointModules;
    }

    @Override
    public Map<String, Module> replaceBean() {
        return super.replaceBean();
    }

    @Override
    public String getBeanName(Class<?> aClass) {
        String value = AnnotationUtils.get(aClass, Aspect.class).value();
        return Assert.isBlankString(value)? BaseUtils.lowercaseFirstLetter(aClass.getSimpleName()):value;
    }
}
