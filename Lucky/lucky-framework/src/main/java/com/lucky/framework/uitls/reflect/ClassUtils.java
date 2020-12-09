package com.lucky.framework.uitls.reflect;

import com.lucky.framework.exception.LuckyReflectionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ClassUtils {

    private static final Logger log= LogManager.getLogger(ClassUtils.class);
    public static final Class<?>[] SIMPLE_CLASSES={String.class,Byte.class,Short.class,Integer.class,
    Long.class,Float.class,Double.class,Boolean.class};

    /**
     * 得到一个类以及所有父类(不包括Object)的所有属性(Field)
     * @param clzz 目标类的Class
     * @return
     */
    public static Field[] getAllFields(Class<?> clzz) {
        if (clzz.getSuperclass() == Object.class) {
            return clzz.getDeclaredFields();
        }
        Field[] clzzFields = clzz.getDeclaredFields();
        Field[] superFields = getAllFields(clzz.getSuperclass());
        return delCoverFields(clzzFields,superFields);
    }

    /**
     * 过滤掉被@Cover注解标注的属性
     * @param thisFields 当前类的所有属性
     * @param superFields 当前类父类的所有属性
     * @return
     */
    private static Field[] delCoverFields(Field[] thisFields,Field[] superFields){
        List<Field> delCvoerFields=new ArrayList<>();
        Set<String> coverFieldNames=new HashSet<>();
        for (Field thisField : thisFields) {
            if(thisField.isAnnotationPresent(Cover.class)){
                coverFieldNames.add(thisField.getName());
            }
            delCvoerFields.add(thisField);
        }
        for (Field superField : superFields) {
            if(!coverFieldNames.contains(superField.getName())){
                delCvoerFields.add(superField);
            }
        }
        return delCvoerFields.toArray(new Field[delCvoerFields.size()]);
    }

    /**
     * 得到一个类以及所有父类(不包括Object)的所有方法(Method)
     * @param clzz 目标类的Class
     * @return
     */
    public static Method[] getAllMethod(Class<?> clzz){
        if (clzz.getSuperclass() == Object.class) {
            return clzz.getDeclaredMethods();
        }
        Method[] clzzMethods = clzz.getDeclaredMethods();
        Method[] superMethods = getAllMethod(clzz.getSuperclass());
        return delCoverMethods(clzzMethods,superMethods);
    }

    /**
     * 过滤掉被@Cover注解标注的方法
     * @param thisMethods 当前类的所有方法
     * @param superMethods 当前类父类的所有方法
     * @return
     */
    private static Method[] delCoverMethods(Method[] thisMethods,Method[] superMethods){
        List<Method> delCoverMethods=new ArrayList<>();
        Set<String> coverMethodNames=new HashSet<>();
        for (Method thisMethod : thisMethods) {
            if(thisMethod.isAnnotationPresent(Cover.class)){
                coverMethodNames.add(thisMethod.getName());
            }
            delCoverMethods.add(thisMethod);
        }
        for (Method superMethod : superMethods) {
            if(!coverMethodNames.contains(superMethod.getName())){
                delCoverMethods.add(superMethod);
            }
        }
        return delCoverMethods.toArray(new Method[delCoverMethods.size()]);
    }

    /**
     * 使用反射机制调用构造函数创建一个对象
     * @param tclass 目标对象的Class
     * @param cparams 构造器执行的参数
     * @param <T>
     * @return
     */
    public static <T> T newObject(Class<? extends T> tclass,Object...cparams){
        try {
            Constructor<? extends T> constructor  =tclass.getConstructor(array2Class(cparams));
            constructor.setAccessible(true);
            return constructor.newInstance(cparams);
        } catch (NoSuchMethodException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error(lex);
            throw lex;
        } catch (IllegalAccessException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error(lex);
            throw lex;
        } catch (InstantiationException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error(lex);
            throw lex;
        } catch (InvocationTargetException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error(lex);
            throw lex;
        }

    }

    /**
     * 将一个Object[]转化为对应类型的Class[]
     * @param objs 要操作的Object[]
     * @return
     */
    public static Class<?>[] array2Class(Object[] objs){
        Class<?>[] paramsClass=new Class<?>[objs.length];
        for (int i = 0; i < objs.length; i++) {
            paramsClass[i]=objs[i].getClass();
        }
        return paramsClass;
    }

    /**
     * 得到某个泛型Type的所有泛型类型
     * @param type 泛型Type
     * @return
     */
    public static Class<?>[] getGenericType(Type type){
        if(type!=null && type instanceof ParameterizedType){
            ParameterizedType pt=(ParameterizedType) type;
            Type[] types=pt.getActualTypeArguments();
            Class<?>[] genericType=new Class<?>[types.length];
            for(int i=0;i<types.length;i++) {
                genericType[i]=(Class<?>)types[i];
            }
            return genericType;
        }else{
            return null;
        }
    }

    /**
     * 判断某个类型是否为JDK自带的类型
     * @param clzz 目标类型
     * @return
     */
    public static boolean isBasic(Class<?> clzz){
        return clzz.getClassLoader()==null;
    }

    /**
     * 根据类的全路径得到一个Class
     * @param className 类的全路径
     * @return
     */
    public static Class<?> getClass(String className){
        try {
            Class<?> aClass = Class.forName(className);
            return aClass;
        } catch (ClassNotFoundException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error(lex);
            throw lex;
        }
    }

    public static Object newObject(String fullPath){
        return newObject(getClass(fullPath));
    }

    public static Object newObject(String fullPath,Object...params){
        return newObject(getClass(fullPath),params);
    }

    /**
     * 得到一个类中被特定注解标注的所有属性
     * @param clzz 类CLass
     * @param annotation 注解类型
     * @return 被注解标注的所有Field
     */
    public static List<Field> getFieldByAnnotation(Class<?> clzz, Class<? extends Annotation> annotation){
        Field[] allFields = getAllFields(clzz);
        List<Field> annFields=new ArrayList<>();
        for (Field field : allFields) {
            if(AnnotationUtils.isExist(field,annotation)){
                annFields.add(field);
            }
        }
        return annFields;
    }

    public static List<Field> getFieldByAnnotationArrayOR(Class<?> clzz, Class<? extends Annotation>[] annotationArray){
        Field[] allFields = getAllFields(clzz);
        List<Field> annFields=new ArrayList<>();
        for (Field field : allFields) {
            if(AnnotationUtils.isExistOrByArray(field,annotationArray)){
                annFields.add(field);
            }
        }
        return annFields;
    }

    /**
     * 得到一个类中被特定注解标注的所有属性(包括注解中的组合注解)
     * @param clzz 类CLass
     * @param annotation 注解类型
     * @return 被注解标注的所有Field
     */
    public static List<Field> getFieldByStrengthenAnnotation(Class<?> clzz, Class<? extends Annotation> annotation){
        Field[] allFields = getAllFields(clzz);
        List<Field> annFields=new ArrayList<>();
        for (Field field : allFields) {
            if(AnnotationUtils.strengthenIsExist(field,annotation)){
                annFields.add(field);
            }
        }
        return annFields;
    }


    /**
     * 得到一个类中被特定注解标注的所有方法
     * @param clzz 类CLass
     * @param annotation 注解类型
     * @return 被注解标注的所有Method
     */
    public static List<Method> getMethodByAnnotation(Class<?> clzz, Class<? extends Annotation> annotation){
        Method[] allMethods = getAllMethod(clzz);
        List<Method> annMethods=new ArrayList<>();
        for (Method method : allMethods) {
            if(AnnotationUtils.isExist(method,annotation)){
                annMethods.add(method);
            }
        }
        return annMethods;
    }

    public static List<Method> getMethodByAnnotationArrayOR(Class<?> clzz, Class<? extends Annotation>[] annotationArray){
        Method[] allMethod = getAllMethod(clzz);
        List<Method> annMethods=new ArrayList<>();
        for (Method method : allMethod) {
            if(AnnotationUtils.isExistOrByArray(method,annotationArray)){
                annMethods.add(method);
            }
        }
        return annMethods;
    }

    /**
     * 得到一个类中被特定注解标注的所有方法(包括注解中的组合注解)
     * @param clzz 类CLass
     * @param annotation 注解类型
     * @return 被注解标注的所有Method
     */
    public static List<Method> getMethodByStrengthenAnnotation(Class<?> clzz, Class<? extends Annotation> annotation){
        Method[] allMethods = getAllMethod(clzz);
        List<Method> annMethods=new ArrayList<>();
        for (Method method : allMethods) {
            if(AnnotationUtils.strengthenIsExist(method,annotation)){
                annMethods.add(method);
            }
        }
        return annMethods;
    }

    /**
     * 判断当前类型是否为Java基本类型
     * @param aClass 当前类型
     * @return
     */
    public static boolean isPrimitive(Class<?> aClass){
        return aClass.isPrimitive();
    }

    /**
     * 判断当前类型是否为Java基本类型的包装类型
     * @param aClass 当前类型
     * @return
     */
    public static boolean isSimple(Class<?> aClass){
        for (Class<?> simpleClass : SIMPLE_CLASSES) {
            if(aClass==simpleClass){
                return true;
            }
        }
        return false;
    }

    public static final Class<?>[] SIMPLE_ARRAY_CLASSES={
            String[].class ,  Byte[].class ,  Short[].class ,Integer[].class,
              Long[].class , Float[].class , Double[].class ,Boolean[].class,
              char[].class ,  byte[].class ,  short[].class ,    int[].class,
              long[].class , float[].class , double[].class ,boolean[].class
    };

    /**
     * 判断当前类型是否为Java基本类型的包装类型
     * @param aClass 当前类型
     * @return
     */
    public static boolean isSimpleArray(Class<?> aClass){
        for (Class<?> simpleClass : SIMPLE_ARRAY_CLASSES) {
            if(aClass==simpleClass){
                return true;
            }
        }
        return false;
    }

    public static boolean isAssignableFromArrayOr(Class<?> targetClass,Class<?>[] arrayClass){
        for (Class<?> aClass : arrayClass) {
            if(aClass.isAssignableFrom(targetClass)){
                return true;
            }
        }
        return false;
    }
}
