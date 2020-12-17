package com.lucky.utils.conversion.proxy;

import com.lucky.utils.conversion.EntityAndDto;
import com.lucky.utils.conversion.LuckyConversion;
import com.lucky.utils.conversion.annotation.Mapping;
import com.lucky.utils.conversion.annotation.Mappings;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 得到一个LuckyConversion接口的实现类JDK实现
 */
public class EDProxy extends Conversion {
    /*
        一、Entity与Dto之间的转换模式：
        1.编写转换接口，该接口必须继承LuckyConversion<E,D>接口，并指定接口的泛型！
        2.如果待转换的Entity与Dto之间的属性完全相同则不需要配置！
        3.指定属性之间相互转换需要借助@Mappings或者@Mapping注解，设置source和target属性指定原字段与目标字段！
        4.如果带转化的对象之中包含其他带转化对象，可以使用@Conversion注解将该对象的LuckyConversion接口实现类注入，该对象的转化交由此Conversion来转换

        二、Conversion接口代理对象的获取
        1.传入一个LuckyConversion接口的子接口的Class
        2.方法内部会获取到该接口的父接口LuckyConversion中的两个泛型
        3.在执行toDto和toEntity方法时会分别做不同的代理
        4.以toDto方法的执行过程为例
            1.找出该接口上@Conversion中配置的LuckyConversion数组
            2.根据这个数组得到一个EntityAndDto集合，EntityAndDto对象中封装的是LuckyConversion的代理对象、Dto泛型和Entity泛型
            3.从传入的参数中获取待转换的Entity对象，并将这个对象转化为一个"全属性名"和属性值组成的Map<String,Object>
            注:原对象中每个自定义类型都会特别生成一个“全类名”=“对象值”的K-V，每个泛型为自定义类型的集合也会特别生成一个“Collection<全类名>”=“集合值”的K-V
            eg：
                Map ==>{
                            name=Jack,(普通属性)
                            age=24,
                            type.name=TYPE-NAME,（嵌套在对象中的属性）
                            com.lucky.Type=com.lucky.Type@a09ee92,(自定义的类型)
                            Collection<com.lucky.Type>=[com.lucky.Type@a04eg12]（泛型为自定义类型的集合）
                        }
            4.检查方法上@Mapping注解或者@Mappings注解中配置的转换映射（source->target），并使用映射Value代替Map中的映射Key，以达到特殊映射的目的
            5.通过反射创建一个空的Dto对象，遍历这个对象的所有属性，并使用属性的“全属性名”去Map中拿到该属性的值，
            首先会检查这个类型的LuckyConversion是否已被配置在@Conversion注解中，如果存在，则调用该LuckyConversion对象执行对这个entity的转换,
            否则创建一个空对象，继续遍历这个属性对象的所有属性
            6.如果是泛型为定义类型集合，则去找对应的LuckyConversion，找不到则会抛出异常！
     */


    /**
     * 得到一个LuckyConversion接口子接口的代理对象
     * @param childInterfaceClass LuckyConversion子接口的Class
     * @param <T>
     * @return
     */
    public static <T extends LuckyConversion> T getProxy(Class<T> childInterfaceClass){
        Type[] luckyConversionGenericClass=childInterfaceClass.getGenericInterfaces();
        ParameterizedType interfaceType=(ParameterizedType) luckyConversionGenericClass[0];
        Class<?> entityClass =(Class<?>) interfaceType.getActualTypeArguments()[0];
        Class<?> dtoClass =(Class<?>) interfaceType.getActualTypeArguments()[1];
        Class<? extends LuckyConversion>[] luckyConversionClasses=childInterfaceClass.getAnnotation(com.lucky.utils.conversion.annotation.Conversion.class).value();
        Class<?>[] interfaces={childInterfaceClass};
        InvocationHandler myInvocationHandler=(proxy,method,params)->{
            String methodName=method.getName();
            if("toEntity".equals(methodName)){
                return change(method,params[0],luckyConversionClasses,false,entityClass);
            }else if("toDto".equals(methodName)){
                return change(method,params[0],luckyConversionClasses,true,dtoClass);
            }else {
                return method.invoke(proxy,params);
            }
        };
        return (T) Proxy.newProxyInstance(childInterfaceClass.getClassLoader(), interfaces, myInvocationHandler);
    }
    
    
    /**
     * 原对象转目标对象
     * @param method 调用的方法
     * @param sourceObj 原对象
     * @param luckyConversionClasses @Conversion注解中的LuckyConversion的Class[]
     * @param toDto 当前执行的方法是否为toDto
     * @param targetClass 目标对象的CLass
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     */
    private static Object change(Method method, Object sourceObj, Class<? extends LuckyConversion>[] luckyConversionClasses,boolean toDto,Class<?> targetClass) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        List<EntityAndDto> eds=getEntityAndDtoByConversion(luckyConversionClasses);
        Constructor<?> constructor = targetClass.getConstructor();
        constructor.setAccessible(true);
        Object targetObj=constructor.newInstance();
        Map<String,Object> sourceFieldNameValueMap =getSourceNameValueMap(sourceObj,"");
        Map<String,String> changeMap=new HashMap<>();
        if(method.isAnnotationPresent(Mapping.class)){
            Mapping mapping=method.getAnnotation(Mapping.class);
            changeMap.put(mapping.source(),mapping.target());
        }else if(method.isAnnotationPresent(Mappings.class)){
            Mapping[] mappings=method.getAnnotation(Mappings.class).value();
            for(Mapping mapping:mappings)
                changeMap.put(mapping.source(),mapping.target());
        }

        Set<String> keySet=changeMap.keySet();
        for(String key:keySet){
            if(sourceFieldNameValueMap.containsKey(key)){
                Object changeValue=sourceFieldNameValueMap.get(key);
                sourceFieldNameValueMap.remove(key);
                sourceFieldNameValueMap.put(changeMap.get(key),changeValue);
            }
        }
        return setTargetObject(targetObj,sourceFieldNameValueMap,eds,toDto,"");
    }
    
}
