package com.lucky.utils.config;

import com.lucky.utils.base.Assert;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/18 0018 10:24
 */
public class MapConfigAnalysis {


    public<T> void mapInit(Map<String,Object> mapConf,T type){
        Class<?> thisClass = type.getClass();
        List<Field> confFields = ClassUtils.getFieldByAnnotation(thisClass, Value.class);
        String key;
        for (Field field : confFields) {
            key=getKeyByField(field);
            if(!mapConf.containsKey(key)){
                continue;
            }
            Class<?> fieldType = field.getType();
            Object confValue=mapConf.get(key);
            try {
                //Class类型
                if(Class.class==fieldType){
                    FieldUtils.setValue(type,field,ClassUtils.getClass(confValue.toString()));
                    continue;
                }
                //基本类型以及基本类型的包装类型
                if(ClassUtils.isPrimitive(fieldType)||ClassUtils.isSimple(fieldType)){
                    FieldUtils.setValue(type,field, JavaConversion.strToBasic(confValue.toString(),fieldType));
                    continue;
                }
                //基本类型以及其包装类型的数组
                if(ClassUtils.isSimpleArray(fieldType)){
                    List<String> confList= (List<String>) confValue;
                    String[] confArr=new String[confList.size()];
                    confList.toArray(confArr);
                    FieldUtils.setValue(type,field,JavaConversion.strArrToBasicArr(confArr,fieldType));
                    continue;
                }

                //集合类型
                if(Collection.class.isAssignableFrom(fieldType)){
                    Class<?> genericType = ClassUtils.getGenericType(field.getGenericType())[0];
                    //泛型为基本类型
                    List<String> confList= (List<String>) confValue;
                    if(ClassUtils.isSimple(genericType)){
                        String[] confArr=new String[confList.size()];
                        confList.toArray(confArr);
                        if(List.class.isAssignableFrom(fieldType)){
                            FieldUtils.setValue(type,field, Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toList()));
                            continue;
                        }
                        if(Set.class.isAssignableFrom(fieldType)){
                            FieldUtils.setValue(type,field, Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toSet()));
                            continue;
                        }
                    }
                    //泛型为Class
                    if(Class.class==genericType){
                        Class<?>[] classes=new Class[confList.size()];
                        for (int i = 0,j=classes.length; i < j; i++) {
                            classes[i]=ClassUtils.getClass(confList.get(i));
                        }
                        if(List.class.isAssignableFrom(fieldType)){
                            FieldUtils.setValue(type,field, Stream.of(classes).collect(Collectors.toList()));
                            continue;
                        }
                        if(Set.class.isAssignableFrom(fieldType)){
                            FieldUtils.setValue(type,field, Stream.of(classes).collect(Collectors.toSet()));
                            continue;
                        }
                    }
                }
                FieldUtils.setValue(type,field,ClassUtils.newObject(confValue.toString()));
            }catch (Exception e){
                throw new MapConfigAnalysisException("解析配置文件时出错：无法将配置 `"+key+"` = `"+confValue+"` 解析为属性["+field+"]",e);
            }
        }
    }


    public void mapInit(Map<String,Object> mapConf){
        mapInit(mapConf,this);
    }

    public<T> T mapInit(Map<String,Object> mapConf,Class<T> typeClass){
        T t = ClassUtils.newObject(typeClass);
        mapInit(mapConf,t);
        return t;
    }

    public String getKeyByField(Field field){
        if(AnnotationUtils.isExist(field, Value.class)){
            String value=AnnotationUtils.get(field,Value.class).value();
            return Assert.isBlankString(value)?field.getName():value;
        }
        return field.getName();
    }
}
