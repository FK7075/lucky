package com.lucky.utils.conversion.proxy;

import com.lucky.utils.conversion.LuckyConversion;
import com.lucky.utils.proxy.CglibProxy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 得到一个LuckyConversion接口的实现类Cglib实现
 */
public class ConversionProxy {

    private  static Map<String,Object> conversionMap;

    private static ConversionMethodInterceptor conversionMethodInterceptor;

    /**
     * 得到一个LuckyConversion接口子接口的代理对象
     * @param childInterfaceClass LuckyConversion子接口的Class
     * @param <T>
     * @return
     */
    public static<T extends LuckyConversion> T getLuckyConversion(Class<T> childInterfaceClass){
        if(conversionMap==null)
            conversionMap=new HashMap<>();
        String mapKey=childInterfaceClass.getName();
        if(conversionMap.containsKey(mapKey))
            return (T) conversionMap.get(mapKey);
        Type[] luckyConversionGenericClass=childInterfaceClass.getGenericInterfaces();
        ParameterizedType interfaceType=(ParameterizedType) luckyConversionGenericClass[0];
        Type[] interfacesTypes=interfaceType.getActualTypeArguments();
        Class<? extends LuckyConversion>[] luckyConversionClasses=childInterfaceClass.getAnnotation(com.lucky.utils.conversion.annotation.Conversion.class).value();
        conversionMethodInterceptor=new ConversionMethodInterceptor(luckyConversionClasses,(Class<?>)interfacesTypes[0],(Class<?>)interfacesTypes[1]);
        T luckyConversion= CglibProxy.getCglibProxyObject(childInterfaceClass,conversionMethodInterceptor);
        conversionMap.put(mapKey,luckyConversion);
        return  luckyConversion;
    }

    public static Map<String,Object> getSourceNameValueMap(Object sourceObject, String initialName) throws IllegalAccessException {
        return conversionMethodInterceptor.getSourceNameValueMap(sourceObject, initialName);
    }
}