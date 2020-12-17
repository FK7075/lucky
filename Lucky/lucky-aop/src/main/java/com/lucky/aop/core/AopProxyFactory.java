package com.lucky.aop.core;


import com.lucky.aop.annotation.Expand;
import com.lucky.framework.container.Module;
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

    public static  Set<InjectionAopPoint> injectionAopPointSet=new HashSet<>(10);

    public static List<PointRun> createPointRuns(Class<?> AspectClass) {
        List<PointRun> pointRuns = new ArrayList<>();
        Object Aspect = ClassUtils.newObject(AspectClass);
        Method[] AspectMethods = AspectClass.getDeclaredMethods();
        for (Method method : AspectMethods) {
            if(AnnotationUtils.strengthenIsExist(method,Expand.class)){
                pointRuns.add(new PointRun(Aspect, method));
            }
        }
        return pointRuns;
    }

    public static boolean isAgent(Class<?> beanClass){
        for (InjectionAopPoint iaPoint : injectionAopPointSet) {
            if(iaPoint.pointCutClass(beanClass)){
                return true;
            }
        }
        return false;
    }

    /**
     * 执行代理
     * @param pointRunCollection 所有的Aspect组件
     * @param module   当前组件
     */
    public static Object aspect(Collection<PointRun> pointRunCollection,Module module) {
        List<PointRun> findPointByBean = findPointbyBean(pointRunCollection, module);
        Class<?> beanClass=module.getOriginalType();
        if (!findPointByBean.isEmpty()||isAgent(beanClass)) {
            return PointRunFactory.createProxyFactory().getProxy(beanClass, findPointByBean);
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
    public static List<PointRun> findPointbyBean(Collection<PointRun> pointRunCollection, Module module) {
        List<PointRun> pointRuns = new ArrayList<>();
        for (PointRun pointRun : pointRunCollection) {
            if (pointRun.classExamine(module)) {
                pointRuns.add(pointRun);
            }
        }
        return pointRuns;
    }
}
