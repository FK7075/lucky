package com.lucky.aop.beanfactory;

import com.lucky.aop.annotation.Aspect;
import com.lucky.aop.annotation.Expand;
import com.lucky.aop.annotation.Pointcut;
import com.lucky.aop.aspectj.constant.AspectJ;
import com.lucky.aop.core.AopPoint;
import com.lucky.aop.core.AopProxyFactory;
import com.lucky.aop.core.InjectionAopPoint;
import com.lucky.aop.core.PointRun;
import com.lucky.aop.utils.PointRunUtils;
import com.lucky.framework.container.FusionStrategy;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.AopBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.utils.base.Assert;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.proxy.JDKProxy;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log= LoggerFactory.getLogger("c.l.f.beanfactory.LuckyAopBeanFactory");
    private final Set<PointRun> pointRunSet;
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

        //扫描得到使用Lucky定义的@Aspect组件定义的切面
        List<Class<?>> aspectPluginClasses = getPluginByAnnotation(Aspect.class)
                .stream()
                .filter(c->!AopPoint.class.isAssignableFrom(c))
                .collect(Collectors.toList());
        for (Class<?> aspectPluginClass : aspectPluginClasses) {
            Object aspectObject = ClassUtils.newObject(aspectPluginClass);
            if(!isIOCId(getBeanName(aspectPluginClass))){
                pointModules.add(new Module(getBeanName(aspectPluginClass),"aspect",aspectObject));
            }

            //解析切面中所有的@Pointcut
            List<Method> pointcutMethod = ClassUtils.getMethodByAnnotation(aspectPluginClass, Pointcut.class);
            for (Method method : pointcutMethod) {
                PointRunUtils.addPointcutExecution(aspectPluginClass.getName()+"."+method.getName()+"()",
                        method.getAnnotation(Pointcut.class).value());
            }

            //解析切面中的通知
            List<Method> expandMethods = ClassUtils.getMethodByStrengthenAnnotation(aspectPluginClass, Expand.class);
            for (Method expandMethod : expandMethods) {
                pointRunSet.add(new PointRun(aspectObject,expandMethod));
            }
        }

        //扫描得到使用AspectJ定义的@Aspect组件
        List<Module> aspectJBeans = getBeanByAnnotation(org.aspectj.lang.annotation.Aspect.class);
        for (Module aspectJBean : aspectJBeans) {
            Class<?> aspectJBeanClass = aspectJBean.getOriginalType();

            //解析切面中所有的@Pointcut
            List<Method> pointcutMethod = ClassUtils.getMethodByAnnotation(aspectJBeanClass, org.aspectj.lang.annotation.Pointcut.class);
            for (Method method : pointcutMethod) {
                PointRunUtils.addPointcutExecution(aspectJBeanClass.getName()+"."+method.getName()+"()",
                        method.getAnnotation(org.aspectj.lang.annotation.Pointcut.class).value());
            }

            //解析切面中的通知
            List<Method> expandMethods = ClassUtils.getMethodByAnnotationArrayOR(aspectJBeanClass, AspectJ.ASPECTJ_EXPANDS_ANNOTATION);
            for (Method expandMethod : expandMethods) {
                pointRunSet.add(new PointRun(aspectJBean.getComponent(),expandMethod));
            }
        }

        //得到IOC容器中定义的切面组件AopPoint
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

        //便利IOC容器中所有的组件，找到那些需要代理的组件，找到后将对应的切面织入
        //织入原理为Cglib的动态代理，最后将代理对象替换掉IOC容器中的源对象
        for (Module bean : beans) {
            if(CglibProxy.isAgent(bean.getComponent().getClass())){
                continue;
            }
            if(JDKProxy.isAgent(bean.getComponent().getClass())){
                continue;
            }
            Object aspect = AopProxyFactory.aspect(pointRunSet, bean);
            if(CglibProxy.isAgent(aspect.getClass())||JDKProxy.isAgent(aspect.getClass())){
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

    public String getBeanName(Class<?> aClass) {
        String value = AnnotationUtils.get(aClass, Aspect.class).value();
        return Assert.isBlankString(value)? Namer.getBeanName(aClass) :value;
    }
}
