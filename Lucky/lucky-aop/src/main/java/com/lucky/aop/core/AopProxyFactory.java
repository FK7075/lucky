package com.lucky.aop.core;


import com.lucky.aop.annotation.*;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;


/**
 * 代理对象发生器
 *
 * @author fk-7075
 */
public class AopProxyFactory {

    private static final Logger log = LogManager.getLogger(AopProxyFactory.class);

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
     * @param iocCode   当前组件的组件代码(Controller,Service,Repository,Component)
     * @param beanid    当前组件的组件id
     * @param beanClass 当前组件Class
     */
    public static Object aspect(Collection<PointRun> pointRunCollection, String iocCode, String beanid, Class<?> beanClass) {
        List<PointRun> findPointByBean = findPointbyBean(pointRunCollection, iocCode, beanid, beanClass);
        if (!findPointByBean.isEmpty()||isAgent(beanClass)) {
            return PointRunFactory.createProxyFactory().getProxy(beanClass, findPointByBean);
        } else {
            return ClassUtils.newObject(beanClass);
        }
    }

    /**
     * 得到Aspect组件中所有符合bean的组件
     *
     * @param pointRunCollection
     * @param iocCode   当前组件的组件代码(Controller,Service,Repository,Component)
     * @param beanid    当前组件的组件id
     * @param beanClass 当前组件
     * @return
     */
    public static List<PointRun> findPointbyBean(Collection<PointRun> pointRunCollection, String iocCode, String beanid, Class<?> beanClass) {
        List<PointRun> pointRuns = new ArrayList<>();
        String pointCutClass;
        for (PointRun pointRun : pointRunCollection) {
            pointCutClass = pointRun.getPointCutClass().trim();
            if ("*".equals(pointCutClass)) {
                pointRuns.add(pointRun);
            } else if (pointCutClass.startsWith("ann:")) {
                if (standardAnn(pointRun, beanClass)) {
                    pointRuns.add(pointRun);
                }
            } else if (pointCutClass.startsWith("path:")) {
                if (standardPath(pointCutClass.substring(5), beanClass.getName())) {
                    pointRuns.add(pointRun);
                }
            } else if (pointCutClass.startsWith("id:")) {
                if (standardId(pointCutClass.substring(3), beanid)) {
                    pointRuns.add(pointRun);
                }
            } else if (pointCutClass.startsWith("ioc:")) {
                if (standardIocCode(pointCutClass.substring(4), iocCode)) {
                    pointRuns.add(pointRun);
                }
            } else {
                throw new RuntimeException("无法识别的切面配置pointCutClass,正确的pointCutClass必须以[path:,ioc:,id:,ann:]中的一个为前缀！错误位置：" + pointRun.method + " ->@Before/@After/@Around/AfterThrowing/AfterReturning(pointCutClass=>err)");
            }
        }
        return pointRuns;
    }

    /**
     * 检验当前类是否符合ann:配置
     *
     * @param pointRun  一个具体的增强
     * @param beanClass 当前类的Class对象
     * @return
     */
    private static boolean standardAnn(PointRun pointRun, Class<?> beanClass) {
        String pointcut=pointRun.getPointCutClass().trim().substring(4);
        String[] cfgIocCode = pointcut.split(",");
        for (String cfg : cfgIocCode) {
            cfg = cfg.trim();
            try {
                Class<? extends Annotation> aClass = (Class<? extends Annotation>) Class.forName(cfg);
                if (beanClass.isAnnotationPresent(aClass)) {
                    return true;
                }
            } catch (ClassNotFoundException e) {
                throw new AopParamsConfigurationException("错误的pointCutClass配置【ann:】=>找不到注解`" + cfg + "`\"`,错误位置："+pointRun.getMethod(),e);
            } catch (Exception e) {
                throw new AopParamsConfigurationException("错误的pointCutClass配置【ann:】=>该类不是注解类型: `" + cfg + "`,错误位置："+pointRun.getMethod(),e);
            }

        }
        return false;
    }

    /**
     * 检验当前类是否符合path:配置
     *
     * @param pointcut 切面配置
     * @param beanName 当前类的全路径
     * @return
     */
    private static boolean standardPath(String pointcut, String beanName) {
        String[] cfgIocCode = pointcut.split(",");
        for (String cfg : cfgIocCode) {
            cfg = cfg.trim();
            if (cfg.endsWith(".*")) {
                if (beanName.contains(cfg.substring(0, cfg.length() - 2))) {
                    return true;
                }
            } else {
                if (cfg.equals(beanName)) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * 检验当前类是否符合id:配置
     *
     * @param pointcut 切面配置
     * @param beanid   当前类的beanID
     * @return
     */
    private static boolean standardId(String pointcut, String beanid) {
        String[] cfgIocCode = pointcut.split(",");
        for (String cfg : cfgIocCode) {
            cfg = cfg.trim();
            if (cfg.equals(beanid)) {
                return true;
            }
        }
        return false;

    }

    /**
     * 检验当前类是否符合ioc:配置
     *
     * @param pointcut 切面配置
     * @param iocCode  当前类的组件代码
     * @return
     */
    private static boolean standardIocCode(String pointcut, String iocCode) {
        String[] cfgIocCode = pointcut.split(",");
        for (String cfg : cfgIocCode) {
            cfg = cfg.trim();
            if (cfg.equalsIgnoreCase(iocCode)) {
                return true;
            }
        }
        return false;
    }

}
