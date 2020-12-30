package com.lucky.utils.base;

import com.lucky.utils.conversion.proxy.Conversion;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BeanUtils {

    public static <T> T copy(T source){
        try {
            if(source.getClass().getClassLoader()==null)
                return source;
            Object target=source.getClass().newInstance();
            Field[] allFields = ClassUtils.getAllFields(source.getClass());
            for(Field field: allFields){
                field.setAccessible(true);
                if(List.class.isAssignableFrom(field.getType())){
                    List list= (List) field.get(source);
                    field.set(target,list.stream().map(a->copy(a)).collect(Collectors.toList()));
                }else if(Set.class.isAssignableFrom(field.getType())){
                    Set set= (Set) field.get(source);
                    Set newSet=new HashSet();
                    set.stream().forEach(a->newSet.add(copy(a)));
                    field.set(target,newSet);
                }else if(Map.class.isAssignableFrom(field.getType())){
                    Map map= (Map) field.get(source);
                    Map newMap=new HashMap();
                    Set keySet = map.keySet();
                    for(Object key:keySet){
                        newMap.put(copy(key),copy(map.get(key)));
                    }
                    field.set(target,newMap);
                }else if(field.getType().getClassLoader()==null){
                    field.set(target,field.get(source));
                }else{
                    field.set(target,copy(field.get(source)));
                }
            }
            return (T) target;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyProperties(Object source,Object target){
        try {
            Map<String, Object> sourceNameValueMap = Conversion.getSourceNameValueMap(source, "");
            setTargetObj(target,sourceNameValueMap,"");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object setTargetObj(Object target,Map<String,Object> sourceMap,String initialName) throws IllegalAccessException, InstantiationException {
        Field[] targetFields = ClassUtils.getAllFields(target.getClass());
        for(Field field:targetFields){
            field.setAccessible(true);
            String fieldName="".equals(initialName)?field.getName():initialName+"."+field.getName();
            if(FieldUtils.isBasicSimpleType(field)){//JDK类型、JDK待泛型且泛型为基本类型的类型
                if(sourceMap.containsKey(fieldName)){
                    field.set(target,sourceMap.get(fieldName));
                }
            }else if(field.getType().getClassLoader()!=null){
                if(sourceMap.containsKey(field.getType().getName())){//类型相同的自定义类型
                    field.set(target,sourceMap.get(field.getType().getName()));
                }else if(sourceMap.containsKey(fieldName)){//类型不相同，但是属性名匹配的
                    field.set(target,setTargetObj(field.getType().newInstance(),sourceMap,fieldName));
                }
            }
        }
        return target;
    }
}
