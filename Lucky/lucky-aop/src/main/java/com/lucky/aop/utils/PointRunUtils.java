package com.lucky.aop.utils;

import com.lucky.aop.annotation.*;
import com.lucky.aop.aspectj.constant.AspectJ;
import com.lucky.aop.core.PointRun;
import com.lucky.aop.enums.Location;
import com.lucky.utils.reflect.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/8 0008 11:56
 */
public abstract class PointRunUtils {

    private final static Map<String,String> pointcutExecutionMap =new HashMap<>();

    public static void addPointcutExecution(String id,String execution){
        if(pointcutExecutionMap.containsKey(id)){
            throw new RuntimeException("PointExecution 添加异常！`"+id+"`已经存在.");
        }
        pointcutExecutionMap.put(id,execution);
    }


    public static double getPriority(Class<?> aspectClass, Method method){
        if(method.isAnnotationPresent(Priority.class)){
            return method.getAnnotation(Priority.class).value();
        }
        if(aspectClass.isAnnotationPresent(Priority.class)){
            return aspectClass.getAnnotation(Priority.class).value();
        }
        if(AnnotationUtils.isExistOrByArray(aspectClass, PointRun.EXPAND_ANNOTATIONS)){
            Annotation ean = AnnotationUtils.getByArray(method, PointRun.EXPAND_ANNOTATIONS);
            return (double) AnnotationUtils.getValue(ean,"priority");
        }
        return 5;
    }

    public static Location getLocation(Annotation annotation){
        if((annotation instanceof After)||(annotation instanceof org.aspectj.lang.annotation.After)){
            return Location.AFTER;
        }
        if((annotation instanceof Before)||(annotation instanceof org.aspectj.lang.annotation.Before)){
            return Location.BEFORE;
        }
        if((annotation instanceof AfterReturning)||(annotation instanceof org.aspectj.lang.annotation.AfterReturning)){
            return Location.AFTER_RETURNING;
        }
        if((annotation instanceof AfterThrowing)||(annotation instanceof org.aspectj.lang.annotation.AfterThrowing)){
            return Location.AFTER_THROWING;
        }
        if((annotation instanceof Around)||(annotation instanceof org.aspectj.lang.annotation.Around)){
            return Location.AROUND;
        }
        throw new RuntimeException("无法获取Location，错误的Annotation类型："+annotation.annotationType());
    }

    public static String getPointcutExecution(Class<?> aspectClass,Method method,Annotation annotation){
        Class<? extends Annotation> aClass = annotation.annotationType();
        String pointcutId;
        if(AnnotationUtils.isExistOrByArray(method,AspectJ.ASPECTJ_EXPANDS_ANNOTATION)){
            pointcutId = (String) AnnotationUtils.getValue(annotation, "value");
        }else if(AnnotationUtils.isExistOrByArray(method,PointRun.EXPAND_ANNOTATIONS)){
            pointcutId = (String) AnnotationUtils.getValue(annotation, "expression");
        }else{
            throw new RuntimeException();
        }
        //1.检查pointcutId是否已经注册
        if(pointcutExecutionMap.containsKey(pointcutId)){
            return pointcutExecutionMap.get(pointcutId);
        }
        //2.方式一的简写方式验证
        String full= aspectClass.getName()+"."+pointcutId;
        if (pointcutExecutionMap.containsKey(full)){
            return pointcutExecutionMap.get(full);
        }
        //没有注册则视为表达式
        return pointcutId;
    }

    private static boolean inAnnotationArray(Class<? extends Annotation>[] array,Class<?extends Annotation> annClass){
        for (Class<? extends Annotation> aClass : array) {
            if(aClass.equals(annClass)){
                return true;
            }
        }
        return false;
    }
}
