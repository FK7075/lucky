package com.lucky.utils.conversion.proxy;

import com.lucky.utils.conversion.EntityAndDto;
import com.lucky.utils.conversion.LuckyConversion;
import com.lucky.utils.conversion.annotation.NoConversion;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Conversion {

    /**
     *
     * @param sourceObject 原对象
     * @param initialName 初始属性名，用于递归时获取带层级的属性名
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String,Object> getSourceNameValueMap(Object sourceObject, String initialName) throws IllegalAccessException {
        Map<String,Object> sourceNameValueMap=new HashMap<>();
        Class<?> sourceClass=sourceObject.getClass();
        Field[] fields= ClassUtils.getAllFields(sourceClass);
        Object fieldValue;
        String fieldName;
        for(Field field:fields){
            if(field.isAnnotationPresent(NoConversion.class)){
                continue;
            }
            fieldValue= FieldUtils.getValue(sourceObject,field);
            fieldName="".equals(initialName)?field.getName():initialName+"."+field.getName();
            if(FieldUtils.isBasicSimpleType(field)){
                sourceNameValueMap.put(fieldName,fieldValue);
            }else if(field.getType().getClassLoader()!=null){
                sourceNameValueMap.put(field.getType().getName(),fieldValue);
                Map<String, Object> fieldNameValueMap = getSourceNameValueMap(fieldValue, fieldName);
                for(String key:fieldNameValueMap.keySet()){
                    sourceNameValueMap.put(key,fieldNameValueMap.get(key));
                }
            }else if(Collection.class.isAssignableFrom(field.getType())){
                Class<?> genericClass=FieldUtils.getGenericType(field)[0];
                sourceNameValueMap.put("Collection<"+genericClass.getName()+">",fieldValue);
            }
        }
        return sourceNameValueMap;
    }


    /**
     * 为目标对象设置属性
     * @param targetObject 目标对象
     * @param sourceMap 原对象的属性名与属性值所组成的Map
     * @param eds EntityAndDto对象集合
     * @param toDto 当前执行的方法是否为toDto
     * @param initialName 初始属性名，用于递归时获取带层级的属性名
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Object setTargetObject(Object targetObject, Map<String, Object> sourceMap, List<EntityAndDto> eds, boolean toDto, String initialName) throws IllegalAccessException, InstantiationException {
        Class<?> targetClass = targetObject.getClass();
        Field[] targetFields=ClassUtils.getAllFields(targetClass);
        Class<?> fieldClass;
        String fieldName;
        for(Field field:targetFields){
            fieldClass=field.getType();
            field.setAccessible(true);
            fieldName="".equals(initialName)?field.getName():initialName+"."+field.getName();
            if(FieldUtils.isBasicSimpleType(field)){
                if(sourceMap.containsKey(fieldName)){
                    field.set(targetObject,sourceMap.get(fieldName));
                }
            }else if(fieldClass.getClassLoader()!=null){
                Object fieldValue=field.getType().newInstance();
                EntityAndDto ed=toDto?EntityAndDto.getEntityAndDtoByDaoClass(eds,field.getType()):EntityAndDto.getEntityAndDtoByEntityClass(eds,field.getType());
                if(ed!=null){
                    String classKey;
                    LuckyConversion conversion=ed.getConversion();
                    if(toDto){//Entity转Dto
                        classKey=ed.getEntityClass().getName();
                        if(sourceMap.containsKey(classKey)){
                            field.set(targetObject,conversion.toDto(sourceMap.get(classKey)));
                        }else {
                            field.set(targetObject,setTargetObject(fieldValue,sourceMap,eds,true,fieldName));
                        }
                    }else{//Dto转Entity
                        classKey=ed.getDtoClass().getName();
                        if(sourceMap.containsKey(classKey)){
                            field.set(targetObject,conversion.toEntity(sourceMap.get(classKey)));
                        }else {
                            field.set(targetObject,setTargetObject(fieldValue,sourceMap,eds,false,fieldName));
                        }
                    }
                }else{
                    field.set(targetObject,setTargetObject(fieldValue,sourceMap,eds,toDto,fieldName));
                }
            }else if(Collection.class.isAssignableFrom(field.getType())){
                Class<?> genericClass=FieldUtils.getGenericType(field)[0];
                String collectionClassKey="Collection<"+genericClass.getName()+">";
                if(sourceMap.containsKey(collectionClassKey)){
                    Collection source = (Collection) sourceMap.get(collectionClassKey);
                    if(List.class.isAssignableFrom(field.getType())){
                        field.set(targetObject,new ArrayList<>(source));
                    }else if(Set.class.isAssignableFrom(field.getType())){
                        field.set(targetObject,new HashSet(source));
                    }
                    continue;
                }
                EntityAndDto ed;
                String classKey;
                LuckyConversion conversion;
                if(toDto){
                    ed=EntityAndDto.getEntityAndDtoByDaoClass(eds,genericClass);
                    if(ed==null) {
                        throw new RuntimeException("在@Conversion注解中找不到"+genericClass+"类相对应的LuckyConversion，无法转换属性（"+field.getType()+"）"+field.getName());
                    }
                    conversion=ed.getConversion();
                    classKey="Collection<"+ed.getEntityClass().getName()+">";
                    if(sourceMap.containsKey(classKey)){
                        Collection coll=(Collection) sourceMap.get(classKey);
                        Object collect = coll.stream().map(a -> conversion.toDto(a)).collect(Collectors.toList());
                        if(List.class.isAssignableFrom(field.getType())) {
                            field.set(targetObject,collect);
                        } else if(Set.class.isAssignableFrom(field.getType())) {
                            field.set(targetObject,new HashSet((List)collect));
                        }
                    }
                }else{
                    ed=EntityAndDto.getEntityAndDtoByEntityClass(eds,genericClass);
                    conversion=ed.getConversion();
                    classKey="Collection<"+ed.getDtoClass().getName()+">";
                    if(sourceMap.containsKey(classKey)){
                        Collection coll=(Collection) sourceMap.get(classKey);
                        Object collect = coll.stream().map(a -> conversion.toEntity(a)).collect(Collectors.toList());
                        if(List.class.isAssignableFrom(field.getType())) {
                            field.set(targetObject,collect);
                        } else if(Set.class.isAssignableFrom(field.getType())) {
                            field.set(targetObject,new HashSet((List)collect));
                        }
                    }
                }
            }
        }
        return targetObject;
    }


    public static List<EntityAndDto> getEntityAndDtoByConversion(Class<? extends LuckyConversion>[] conversionClasses){
        List<EntityAndDto> eds=new ArrayList<>();
        LuckyConversion luckyConversion;
        Type[] conversionGenericTypes;
        ParameterizedType interfaceType;
        Class<?> dtoClass;
        Class<?> entityClass;
        for(Class<? extends LuckyConversion> conversionClass:conversionClasses){
            if(conversionClass==LuckyConversion.class) {
                continue;
            }
            luckyConversion=EDProxy.getProxy(conversionClass);
            conversionGenericTypes=conversionClass.getGenericInterfaces();
            interfaceType=(ParameterizedType) conversionGenericTypes[0];
            entityClass=(Class<?>) interfaceType.getActualTypeArguments()[0];
            dtoClass=(Class<?>) interfaceType.getActualTypeArguments()[1];
            eds.add(new EntityAndDto(luckyConversion,entityClass,dtoClass));
        }
        return eds;
    }
}
