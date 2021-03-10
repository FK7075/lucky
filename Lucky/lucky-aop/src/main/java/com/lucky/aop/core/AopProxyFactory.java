package com.lucky.aop.core;


import com.lucky.aop.annotation.Expand;
import com.lucky.framework.container.Module;
import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.proxy.JDKProxy;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;


/**
 * 代理对象发生器
 *
 * @author fk-7075
 */
public class AopProxyFactory {

    private static final Logger log= LoggerFactory.getLogger(AopProxyFactory.class);
    public static  Set<InjectionAopPoint> injectionAopPointSet;
    static {
        injectionAopPointSet=new HashSet<>(10);
    }


    public static List<PointRun> createPointRuns(Class<?> AspectClass) {
        List<PointRun> pointRuns = new ArrayList<>();
        Object Aspect = ClassUtils.newObject(AspectClass);
        Method[] AspectMethods =ClassUtils.getAllMethod(AspectClass);
        for (Method method : AspectMethods) {
            if(AnnotationUtils.strengthenIsExist(method,Expand.class)){
                pointRuns.add(new PointRun(Aspect, method));
            }
        }
        return pointRuns;
    }

    /**
     * 是否为代理对象的Class
     * @param beanClass 待检验的Class
     * @return Y(true)/N(false)
     */
    public static  boolean isProxyObjectClass(Class<?> beanClass){
        return JDKProxy.isAgent(beanClass)|| CglibProxy.isAgent(beanClass);
    }

    /**
     * 判断当前Class是否需要代理
     * @param beanClass 待检验的Class
     * @return Y(true)/N(false)
     */
    public static boolean isNeedProxy(Class<?> beanClass){
        for (InjectionAopPoint iaPoint : injectionAopPointSet) {
            if(iaPoint.pointCutClass(beanClass)){
                return true;
            }
        }
        return false;
    }

    /**
     * 执行代理(注入之前，代理对象支持嵌套执行)
     * @param pointRunCollection 所有的Aspect组件
     * @param module   当前组件
     * @return 支持嵌套的代理对象
     */
    public static Object preInjectionProxy(Collection<PointRun> pointRunCollection,Module module) {
        List<PointRun> findPointByBean = findPointByBean(pointRunCollection, module);
        Class<?> beanClass=module.getOriginalType();
        if (!findPointByBean.isEmpty()||isNeedProxy(beanClass)) {
            return ProxyClassFactory.createProxyFactory().getProxy(module.getComponent(), findPointByBean);
        } else {
            return module.getComponent();
        }
    }

    /**
     *  执行代理(注入之后，代理对象不支持嵌套执行)
     * @param pointRunCollection 所有的Aspect组件
     * @param module 当前组件
     * @return 不支持嵌套的代理对象
     */
    public static Object postInjectionProxy(Collection<PointRun> pointRunCollection,Module module){
        List<PointRun> findPointByBean = findPointByBean(pointRunCollection, module);
        Class<?> beanClass=module.getOriginalType();
        if (!findPointByBean.isEmpty()||isNeedProxy(beanClass)) {
            return ProxyObjectFactory.getProxyFactory().getProxyObject(module.getComponent(), findPointByBean);
        } else {
            return module.getComponent();
        }
    }

    /**
     * 得到Aspect组件中所有符合bean的组件
     *
     * @param pointRunCollection
     * @param module   当前组件
     * @return
     */
    public static List<PointRun> findPointByBean(Collection<PointRun> pointRunCollection, Module module) {
        List<PointRun> pointRuns = new ArrayList<>();
        for (PointRun pointRun : pointRunCollection) {
            if (pointRun.classExamine(module)) {
                pointRuns.add(pointRun);
            }
        }
        return pointRuns;
    }
}
