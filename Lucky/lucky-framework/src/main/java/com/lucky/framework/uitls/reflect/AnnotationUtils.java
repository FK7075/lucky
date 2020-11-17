package com.lucky.framework.uitls.reflect;

import com.lucky.framework.exception.LuckyReflectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/10/26 16:33
 */
public abstract class AnnotationUtils {

    private static final Logger log= LogManager.getLogger(AnnotationUtils.class);

    /**
     * 判断类是否被注解标注
     * @param aClass 目标类的Class
     * @param annotationClass 注解Class
     * @return true/false
     */
    public static boolean isExist(Class<?> aClass,Class<? extends Annotation> annotationClass){
        return aClass.isAnnotationPresent(annotationClass);
    }

    /**
     * 判断方法是否被注解标注
     * @param method 目标方法的Method
     * @param annotationClass 注解Class
     * @return true/false
     */
    public static boolean isExist(Method method, Class<? extends Annotation> annotationClass){
        return method.isAnnotationPresent(annotationClass);
    }

    /**
     * 判断属性是否被注解标注
     * @param field 目标属性的Field
     * @param annotationClass 注解Class
     * @return true/false
     */
    public static boolean isExist(Field field, Class<? extends Annotation> annotationClass){
        return field.isAnnotationPresent(annotationClass);
    }

    /**
     * 判断方法参数是否被注解标注
     * @param parameter 目标方法参数的Parameter
     * @param annotationClass 注解Class
     * @return true/false
     */
    public static boolean isExist(Parameter parameter, Class<? extends Annotation> annotationClass){
        return parameter.isAnnotationPresent(annotationClass);
    }

    /**
     * 判断类是否被注解数组中的某一个标注
     * @param aClass 目标类的Class
     * @param annotationClasses 注解Class数组
     * @return true/false
     */
    public static boolean isExistOrByArray(Class<?> aClass,Class<? extends Annotation>[] annotationClasses){
        for (Class<? extends Annotation> ac : annotationClasses) {
            if(isExist(aClass,ac)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断方法是否被注解数组中的某一个标注
     * @param method 目标方法的Method
     * @param annotationClasses 注解Class数组
     * @return true/false
     */
    public static boolean isExistOrByArray(Method method,Class<? extends Annotation>[] annotationClasses){
        for (Class<? extends Annotation> ac : annotationClasses) {
            if(isExist(method,ac)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断属性是否被注解数组中的某一个标注
     * @param field 目标属性的Field
     * @param annotationClasses 注解Class数组
     * @return true/false
     */
    public static boolean isExistOrByArray(Field field,Class<? extends Annotation>[] annotationClasses){
        for (Class<? extends Annotation> ac : annotationClasses) {
            if(isExist(field,ac)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断方法参数是否被注解数组中的某一个标注
     * @param parameter 目标方法参数的Parameter
     * @param annotationClasses 注解Class数组
     * @return true/false
     */
    public static boolean isExistOrByArray(Parameter parameter,Class<? extends Annotation>[] annotationClasses){
        for (Class<? extends Annotation> ac : annotationClasses) {
            if(isExist(parameter,ac)){
                return true;
            }
        }
        return false;
    }


    /**
     * 得到类上的某个注解的实例
     * @param aClass 目标类的Class
     * @param annotationClass 注解Class
     * @param <T> 注解泛型
     * @return 注解实例
     */
    public static <T extends Annotation> T get(Class<?> aClass,Class<T> annotationClass){
        if(isExist(aClass,annotationClass)){
            return aClass.getAnnotation(annotationClass);
        }
        throw new AnnotationParsingException("获取注解失败 ： 类\""+aClass+"\"并没有被注解[\""+annotationClass+"\"]标注！");
    }

    /**
     * 得到方法上的某个注解的实例
     * @param method 目标方法的Method
     * @param annotationClass 注解Class
     * @param <T> 注解泛型
     * @return 注解实例
     */
    public static <T extends Annotation> T get(Method method,Class<T> annotationClass){
        if(isExist(method,annotationClass)){
            return method.getAnnotation(annotationClass);
        }
        throw new AnnotationParsingException("获取注解失败 ： 方法\""+method+"\"并没有被注解[\""+annotationClass+"\"]标注！");
    }

    /**
     * 得到属性上的某个注解的实例
     * @param field 目标属性的Field
     * @param annotationClass 注解Class
     * @param <T> 注解泛型
     * @return 注解实例
     */
    public static  <T extends Annotation> T get(Field field,Class<T> annotationClass){
        if(isExist(field,annotationClass)){
            return field.getAnnotation(annotationClass);
        }
        throw new AnnotationParsingException("获取注解失败 ： 属性\""+field+"\"并没有被注解[\""+annotationClass+"\"]标注！");
    }

    /**
     * 得到方法参数上的某个注解的实例
     * @param parameter 目标方法参数的Parameter
     * @param annotationClass 注解Class
     * @param <T> 注解泛型
     * @return 注解实例
     */
    public static <T extends Annotation> T get(Parameter parameter,Class<T> annotationClass){
        if(isExist(parameter,annotationClass)){
            return parameter.getAnnotation(annotationClass);
        }
        throw new AnnotationParsingException("获取注解失败 ： 方法参数\""+parameter+"\"并没有被注解[\""+annotationClass+"\"]标注！");
    }

    /**
     * 动态的设置注解某个属性的值
     * @param ann 注解实例
     * @param fileName 需要修改的属性的属性名
     * @param setValue 值
     * @param <Ann>
     */
    public static <Ann extends Annotation> void setValue(Ann ann, String fileName, Object setValue ){
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(ann);
        Field memberValues = FieldUtils.getDeclaredField(invocationHandler.getClass(),"memberValues");
        memberValues.setAccessible(true);
        Map map = null;
        try {
            map = (Map) memberValues.get(invocationHandler);
            map.put(fileName,setValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static  <Ann extends Annotation> Object  getValue(Ann ann, String fileName) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(ann);
        Field memberValues = FieldUtils.getDeclaredField(invocationHandler.getClass(),"memberValues");
        memberValues.setAccessible(true);
        Map map = null;
        try {
            map = (Map) memberValues.get(invocationHandler);
            return map.get(fileName);
        } catch (IllegalAccessException e) {
            throw new LuckyReflectionException(e);

        }
    }

    /**
     * 过滤掉注解中的元注解
     * @param annotations
     * @return
     */
    public static List<Annotation> filterMetaAnnotation(Annotation[] annotations){
        return Stream.of(annotations).filter((a) -> {
            boolean r = a instanceof Retention;
            boolean d = a instanceof Documented;
            boolean t = a instanceof Target;
            return !(r | d | t);
        }).collect(Collectors.toList());
    }

    /**
     * 得到目标类中包含的所有目标注解（包含组合注解中的目标注解）
     * @param aClass 目标类
     * @param annClass 目标注解
     * @param <T> 目标注解的类型
     * @return
     */
    public static <T extends Annotation> List<T> strengthenGet(Class<?> aClass,Class<T> annClass){
        List<T> list=new ArrayList<>();
        List<Annotation> annotations = filterMetaAnnotation(aClass.getAnnotations());
        for (Annotation annotation : annotations) {
            if(annotation.annotationType()==annClass){
                list.add((T) annotation);
            }
            list.addAll(strengthenGet(annotation.annotationType(),annClass));
        }
        return list;
    }

    /**
     * 得到目标属性中包含的所有目标注解（包含组合注解中的目标注解）
     * @param field 目标属性
     * @param annClass 目标注解
     * @param <T> 目标注解的类型
     * @return
     */
    public static <T extends Annotation> List<T> strengthenGet(Field field,Class<T> annClass){
        List<T> list=new ArrayList<>();
        List<Annotation> annotations = filterMetaAnnotation(field.getAnnotations());
        for (Annotation annotation : annotations) {
            if(annotation.annotationType()==annClass){
                list.add((T) annotation);
            }
            list.addAll(strengthenGet(annotation.annotationType(),annClass));
        }
        return list;
    }

    /**
     * 得到目标方法中包含的所有目标注解（包含组合注解中的目标注解）
     * @param method 目标方法
     * @param annClass 目标注解
     * @param <T> 目标注解的类型
     * @return
     */
    public static <T extends Annotation> List<T> strengthenGet(Method method,Class<T> annClass){
        List<T> list=new ArrayList<>();
        List<Annotation> annotations = filterMetaAnnotation(method.getAnnotations());
        for (Annotation annotation : annotations) {
            if(annotation.annotationType()==annClass){
                list.add((T) annotation);
            }
            list.addAll(strengthenGet(annotation.annotationType(),annClass));
        }
        return list;
    }


    /**
     * 加强版的类注解标注检查，针对组合注解的检查
     * @param aClass 目标类的Class
     * @param annClass 注解Class
     * @return true/false
     */
    public static <T extends Annotation>  boolean strengthenIsExist(Class<?> aClass,Class<T> annClass){
        if(isExist(aClass,annClass)){
            return true;
        }
        List<Annotation> annotations = filterMetaAnnotation(aClass.getAnnotations());
        for (Annotation annotation : annotations) {
            if(strengthenIsExist(annotation.annotationType(),annClass)){
                return true;
            }
        }
        return false;
    }

    /**
     * 加强版的类注解标注检查，针对组合注解的检查
     * @param field 目标Field
     * @param annClass 注解Class
     * @return true/false
     */
    public static <T extends Annotation>  boolean strengthenIsExist(Field field,Class<T> annClass){
        if(isExist(field,annClass)){
            return true;
        }
        List<Annotation> annotations = filterMetaAnnotation(field.getAnnotations());
        for (Annotation annotation : annotations) {
            if(strengthenIsExist(annotation.annotationType(),annClass)){
                return true;
            }
        }
        return false;
    }

    /**
     * 加强版的类注解标注检查，针对组合注解的检查
     * @param method 目标Method
     * @param annClass 注解Class
     * @return true/false
     */
    public static <T extends Annotation>  boolean strengthenIsExist(Method method,Class<T> annClass){
        if(isExist(method,annClass)){
            return true;
        }
        List<Annotation> annotations = filterMetaAnnotation(method.getAnnotations());
        for (Annotation annotation : annotations) {
            if(strengthenIsExist(annotation.annotationType(),annClass)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断class是否被注解数组中的其中一个标注
     *  1.没有被任意一个标注（异常）
     *  2.被其中一个标注
     *  3.被其中多个注解标注 （异常）
     * @param aClass 待判断的Class
     * @param annClassArray 注解数组
     * @return
     */
    public static Annotation getByArray(Class<?> aClass,Class<? extends Annotation>[] annClassArray){
        List<Annotation> list=new ArrayList<>();
        for (Class<? extends Annotation> annClass : annClassArray) {
            if(isExist(aClass,annClass)){
                list.add(get(aClass,annClass));
            }
        }
        if(list.isEmpty()){

        }
        if(list.size()!=1){

        }
        return list.get(0);
    }

    /**
     * 判断Method是否被注解数组中的其中一个标注
     *  1.没有被任意一个标注（异常）
     *  2.被其中一个标注
     *  3.被其中多个注解标注 （异常）
     * @param method 待判断的Method
     * @param annClassArray 注解数组
     * @return
     */
    public static Annotation getByArray(Method method,Class<? extends Annotation>[] annClassArray){
        List<Annotation> list=new ArrayList<>();
        for (Class<? extends Annotation> annClass : annClassArray) {
            if(isExist(method,annClass)){
                list.add(get(method,annClass));
            }
        }
        if(list.isEmpty()){

        }
        if(list.size()!=1){

        }
        return list.get(0);
    }

}
