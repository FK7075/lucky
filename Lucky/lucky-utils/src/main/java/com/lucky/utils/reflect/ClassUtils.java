package com.lucky.utils.reflect;

import com.lucky.utils.annotation.Nullable;
import com.lucky.utils.base.Assert;
import com.lucky.utils.exception.LuckyReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public abstract class ClassUtils {

    private static final Logger log= LoggerFactory.getLogger(ClassUtils.class);
    public static final Class<?>[] SIMPLE_CLASSES={String.class,Byte.class,Short.class,Integer.class,
    Long.class,Float.class,Double.class,Boolean.class};
    /** The package separator character: {@code '.'}. */
    private static final char PACKAGE_SEPARATOR = '.';
    /** The path separator character: {@code '/'}. */
    private static final char PATH_SEPARATOR = '/';
    /** A reusable empty class array constant. */
    private static final Class<?>[] EMPTY_CLASS_ARRAY = {};
    /**
     * Map with primitive type as key and corresponding wrapper
     * type as value, for example: int.class -> Integer.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

    /**
     * Map with primitive wrapper type as key and corresponding primitive
     * type as value, for example: Integer.class -> int.class.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

    public static Class<?> forName(String fullPath,ClassLoader loader){
        Assert.notNull(fullPath, "Name must not be null");
        try {
            return Class.forName(fullPath,true,loader);
        } catch (ClassNotFoundException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error("ClassNotFoundException: `"+fullPath+"` 不存在！",lex);
            throw lex;
        }
    }

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

    public static <T> Constructor<T> getConstructor(Class<T> aClass,Class<?>[] paramTypes){
        try {
            return aClass.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error("NoSuchMethodException: 找不到 `"+aClass.getName()+"` 的构造器，无法实例化其对象！",lex);
            throw lex;
        }
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

    //获取类的所有方法,包括继承的父类和实现的接口里面的方法
    public static List<Method> getAllMethodForClass(Class<?> beanClass) {
        List<Method> allMethods = new LinkedList<>();
        //获取beanClass的所有接口
        Set<Class<?>> classes = new LinkedHashSet<>(ClassUtils.getAllInterfacesForClassAsSet(beanClass));
        classes.add(beanClass);

        //遍历所有的类和接口反射获取到所有的方法
        for (Class<?> clazz : classes) {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
            for (Method m : methods) {
                allMethods.add(m);
            }
        }

        return allMethods;
    }



    public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
        if (clazz == null) {
            return "";
        }
        String className = clazz.getName();
        int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        if (packageEndIndex == -1) {
            return "";
        }
        String packageName = className.substring(0, packageEndIndex);
        return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
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
        return delCoverMethods.toArray(new Method[0]);
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
            log.error("NoSuchMethodException: 找不到 `"+tclass.getName()+"` 的无参构造器，无法实例化其对象！",lex);
            throw lex;
        } catch (IllegalAccessException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error("IllegalAccessException",lex);
            throw lex;
        } catch (InstantiationException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error("InstantiationException: 在实例化 `"+tclass.getName()+"` 时出现错误！",lex);
            throw lex;
        } catch (InvocationTargetException e) {
            LuckyReflectionException lex = new LuckyReflectionException(e);
            log.error("InvocationTargetException",lex);
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
            log.error("ClassNotFoundException",lex);
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

    public static boolean isJdkType(Class<?> aClass){
        return aClass.getClassLoader()==null;
    }

    public static String convertClassNameToResourcePath(String className) {
        Assert.notNull(className, "Class name must not be null");
        return className.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }

    public static List<Method> getAllMethods(Class<?> aClass){
        List<Method> allMethods = new LinkedList<>();
        //获取beanClass的所有接口
        Set<Class<?>> classes = new LinkedHashSet<>(getAllInterfacesForClassAsSet(aClass));
        classes.add(aClass);
        //遍历所有的类和接口反射获取到所有的方法
        for (Class<?> clazz : classes) {
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
            allMethods.addAll(Arrays.asList(methods));
        }
        return allMethods;
    }

    @Nullable
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }

    /**
     * Determine whether the {@link Class} identified by the supplied name is present
     * and can be loaded. Will return {@code false} if either the class or
     * one of its dependencies is not present or cannot be loaded.
     * @param className the name of the class to check
     * @param classLoader the class loader to use
     * (may be {@code null} which indicates the default class loader)
     * @return whether the specified class is present (including all of its
     * superclasses and interfaces)
     * @throws IllegalStateException if the corresponding class is resolvable but
     * there was a readability mismatch in the inheritance hierarchy of the class
     * (typically a missing dependency declaration in a Jigsaw module definition
     * for a superclass or interface implemented by the class to be checked here)
     */
    public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        }
        catch (IllegalAccessError err) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" +
                    className + "]: " + err.getMessage(), err);
        }
        catch (Throwable ex) {
            // Typically ClassNotFoundException or NoClassDefFoundError...
            return false;
        }
    }


    /**
     * Determine if the supplied class is an <em>inner class</em>,
     * i.e. a non-static member of an enclosing class.
     * @return {@code true} if the supplied class is an inner class
     * @since 5.0.5
     * @see Class#isMemberClass()
     */
    public static boolean isInnerClass(Class<?> clazz) {
        return (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers()));
    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            return (lhsType == resolvedPrimitive);
        }
        else {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            return (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper));
        }
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return (clazz.isPrimitive() && clazz != void.class ? primitiveTypeToWrapperMap.get(clazz) : clazz);
    }

    public static Class<?> resolveClassName(String className, @Nullable ClassLoader classLoader)
            throws IllegalArgumentException {

        try {
            return forName(className, classLoader);
        }
        catch (IllegalAccessError err) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" +
                    className + "]: " + err.getMessage(), err);
        }
        catch (LinkageError err) {
            throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err);
        }
    }

    public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
        return getAllInterfacesForClass(clazz, null);
    }

    /**
     * Return all interfaces that the given class implements as an array,
     * including ones implemented by superclasses.
     * <p>If the class itself is an interface, it gets returned as sole interface.
     * @param clazz the class to analyze for interfaces
     * @param classLoader the ClassLoader that the interfaces need to be visible in
     * (may be {@code null} when accepting all declared interfaces)
     * @return all interfaces that the given object implements as an array
     */
    public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, @Nullable ClassLoader classLoader) {
        return toClassArray(getAllInterfacesForClassAsSet(clazz, classLoader));
    }

    /**
     * Return all interfaces that the given instance implements as a Set,
     * including ones implemented by superclasses.
     * @param instance the instance to analyze for interfaces
     * @return all interfaces that the given instance implements as a Set
     */
    public static Set<Class<?>> getAllInterfacesAsSet(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClassAsSet(instance.getClass());
    }

    /**
     * Return all interfaces that the given class implements as a Set,
     * including ones implemented by superclasses.
     * <p>If the class itself is an interface, it gets returned as sole interface.
     * @param clazz the class to analyze for interfaces
     * @return all interfaces that the given object implements as a Set
     */
    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz) {
        return getAllInterfacesForClassAsSet(clazz, null);
    }

    /**
     * Return all interfaces that the given class implements as a Set,
     * including ones implemented by superclasses.
     * <p>If the class itself is an interface, it gets returned as sole interface.
     * @param clazz the class to analyze for interfaces
     * @param classLoader the ClassLoader that the interfaces need to be visible in
     * (may be {@code null} when accepting all declared interfaces)
     * @return all interfaces that the given object implements as a Set
     */
    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, @Nullable ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface() && isVisible(clazz, classLoader)) {
            return Collections.singleton(clazz);
        }
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        Class<?> current = clazz;
        while (current != null) {
            Class<?>[] ifcs = current.getInterfaces();
            for (Class<?> ifc : ifcs) {
                if (isVisible(ifc, classLoader)) {
                    interfaces.add(ifc);
                }
            }
            current = current.getSuperclass();
        }
        return interfaces;
    }

    /**
     * Check whether the given class is visible in the given ClassLoader.
     * @param clazz the class to check (typically an interface)
     * @param classLoader the ClassLoader to check against
     * (may be {@code null} in which case this method will always return {@code true})
     */
    public static boolean isVisible(Class<?> clazz, @Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        try {
            if (clazz.getClassLoader() == classLoader) {
                return true;
            }
        }
        catch (SecurityException ex) {
            // Fall through to loadable check below
        }

        // Visible if same Class can be loaded from given ClassLoader
        return isLoadable(clazz, classLoader);
    }

    /**
     * Check whether the given class is loadable in the given ClassLoader.
     * @param clazz the class to check (typically an interface)
     * @param classLoader the ClassLoader to check against
     * @since 5.0.6
     */
    private static boolean isLoadable(Class<?> clazz, ClassLoader classLoader) {
        try {
            return (clazz == classLoader.loadClass(clazz.getName()));
            // Else: different class with same name found
        }
        catch (ClassNotFoundException ex) {
            // No corresponding class found at all
            return false;
        }
    }

    /**
     * Copy the given {@code Collection} into a {@code Class} array.
     * <p>The {@code Collection} must contain {@code Class} elements only.
     * @param collection the {@code Collection} to copy
     * @return the {@code Class} array
     * @since 3.1
     */
    public static Class<?>[] toClassArray(@Nullable Collection<Class<?>> collection) {
        return (!Assert.isEmptyCollection(collection) ? collection.toArray(EMPTY_CLASS_ARRAY) : EMPTY_CLASS_ARRAY);
    }


}
