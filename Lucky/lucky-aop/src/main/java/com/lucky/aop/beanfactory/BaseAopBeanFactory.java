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
import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.proxy.JDKProxy;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/10 0010 17:43
 */
public abstract class BaseAopBeanFactory extends AopBeanFactory {

    private static final Logger log= LoggerFactory.getLogger("c.l.f.beanfactory.BaseAopBeanFactory");
    protected final Set<PointRun> pointRunSet;
    protected final Set<InjectionAopPoint> injectionAopPoints;

    public BaseAopBeanFactory(){
        super();
        pointRunSet=new HashSet<>(30);
        injectionAopPoints=new HashSet<>(10);
    }

    public BaseAopBeanFactory(FusionStrategy fusionStrategy){
        super(fusionStrategy);
        pointRunSet=new HashSet<>(30);
        injectionAopPoints=new HashSet<>(10);
    }

    protected void loadPoint(){
        //扫描得到使用Lucky定义的@Aspect组件定义的切面
        List<Module> aspectBeans = getBeanByAnnotation(Aspect.class);
        for (Module aspectBean : aspectBeans) {
            Class<?> aopBeanClass = aspectBean.getOriginalType();
            if(aspectBean.getComponent() instanceof AopPoint){
                continue;
            }

            //解析切面中所有的@Pointcut
            List<Method> pointcutMethod = ClassUtils.getMethodByAnnotation(aopBeanClass, Pointcut.class);
            for (Method method : pointcutMethod) {
                PointRunUtils.addPointcutExecution(aopBeanClass.getName()+"."+method.getName()+"()",
                        method.getAnnotation(Pointcut.class).value());
            }

            //解析切面中的通知
            List<Method> expandMethods = ClassUtils.getMethodByStrengthenAnnotation(aopBeanClass, Expand.class);
            for (Method expandMethod : expandMethods) {
                pointRunSet.add(new PointRun(aspectBean.getComponent(),expandMethod));
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
            Object aopPoint = aopPointModule.getComponent();
            if(aopPoint instanceof InjectionAopPoint){
                injectionAopPoints.add((InjectionAopPoint)aopPoint);
            }else{
                pointRunSet.add(new PointRun((AopPoint)aopPoint));
            }
        }

        AopProxyFactory.injectionAopPointSet=injectionAopPoints;
    }

    protected void proxy(Collection<Module> beans, boolean isPost){
        //便利IOC容器中所有的组件，找到那些需要代理的组件，找到后将对应的切面织入
        //织入原理为Cglib的动态代理，最后将代理对象替换掉IOC容器中的源对象
        for (Module bean : beans) {
            if(AopProxyFactory.isProxyObjectClass(bean.getComponent().getClass())){
                continue;
            }
            Object aspect =isPost? AopProxyFactory.postInjectionProxy(pointRunSet,bean)
                                 : AopProxyFactory.preInjectionProxy(pointRunSet, bean);
            if(AopProxyFactory.isProxyObjectClass(aspect.getClass())){
                bean.setComponent(aspect);
                log.info("Create Aop Proxy Bean `{}`",bean.getComponent());
            }
        }
    }

    protected String getBeanName(Class<?> aClass) {
        String value = AnnotationUtils.get(aClass, Aspect.class).value();
        return Assert.isBlankString(value)? Namer.getBeanName(aClass) :value;
    }
}
