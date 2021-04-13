package org.luckyframework.beans;

import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.annotation.Value;
import org.luckyframework.beans.factory.ListableBeanFactory;
import org.luckyframework.environment.Environment;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 16:00
 */
@SuppressWarnings("all")
public class ValueInjection implements Injection {

    private final Environment environment;

    public ValueInjection(Environment environment){
        this.environment = environment;
    }

    @Override
    public void injection(Object instance, ListableBeanFactory beanFactory) {
        Class<?> beanClass = instance.getClass();
        List<Field> valueFields = ClassUtils.getFieldByAnnotation(beanClass, Value.class);
        for (Field field : valueFields) {
            Class<?> fieldType = field.getType();
            Value value = field.getAnnotation(Value.class);
            FieldUtils.setValue(instance,field,getRealValue(ResolvableType.forInstance(instance),value));
        }
    }

    public Object getRealValue(ResolvableType rtype, Value value){
        Class<?> type = rtype.getRawClass();
        String exp = value.value();
        Object confValue = environment.parsing(exp);
        if(confValue==null){
            return null;
        }
        //Class类型
        if(Class.class==type){
            return ClassUtils.getClass(confValue.toString());
        }
        //基本类型以及基本类型的包装类型
        if(ClassUtils.isPrimitive(type)||ClassUtils.isSimple(type)){
            return JavaConversion.strToBasic(confValue.toString(), type);
        }

        //基本类型以及其包装类型的数组
        if(ClassUtils.isSimpleArray(type)){
            List<String> confList= (List<String>) confValue;
            return JavaConversion.strArrToBasicArr(listToArrayByStr(confList), type);
        }

        //非JDK类型
        if(!ClassUtils.isJdkType(type)){
//            if(JexlEngineUtil.isExpression(exp)){
//                setField(bean,valueField,getFieldObject(yaml,valueField,fieldType,exp,delimiter));
//            }
            return null;
        }


        //集合类型
        if(Collection.class.isAssignableFrom(type)){
            Class<?> genericType = rtype.resolveGeneric(0);

            //泛型为基本类型
            if(ClassUtils.isSimple(genericType)){
                List<String> confList= (List<String>) confValue;
                String[] confArr=listToArrayByStr(confList);
                if(List.class.isAssignableFrom(type)){
                    return Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toList());
                }
                if(Set.class.isAssignableFrom(type)){
                    return Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toSet());
                }
                return null;
            }

            //泛型为非JDK类型
            if(!ClassUtils.isJdkType(genericType)){
//                if(!JexlEngineUtil.isExpression(exp)){
//                    return null;
//                }
//                List<Object> confList= (List<Object>) confValue;
//                List<Object> confValueList=new ArrayList<>(confList.size());
//                String listExp=exp.substring(2,exp.length()-1).trim();
//                for(int i=0,j=confList.size();i<j;i++){
//                    String pre=listExp+".get("+i+")";
//                    confValueList.add(getFieldObject(yaml,valueField,genericType,pre,delimiter));
//                }
//                setField(bean,valueField,confValueList);
                return null;
            }

            //泛型为Class
            if(Class.class==genericType){
                List<String> confList= (List<String>) confValue;
                Class<?>[] classes=new Class[confList.size()];
                for (int i = 0,j=classes.length; i < j; i++) {
                    classes[i]=ClassUtils.getClass(environment.parsing(confList.get(i)).toString());
                }
                if(List.class.isAssignableFrom(type)){
                    return  Stream.of(classes).collect(Collectors.toList());
                }
                if(Set.class.isAssignableFrom(type)){
                    return Stream.of(classes).collect(Collectors.toSet());
                }
                return null;
            }
        }

        if(Map.class.isAssignableFrom(type)){
            Map<String,Object> $confMap= (Map<String, Object>) confValue;
            Map<String,Object> confMap=new HashMap<>();
            for(Map.Entry<String,Object> entry:$confMap.entrySet()){
                confMap.put(entry.getKey(),environment.parsing(entry.getValue()));
            }
            return confMap;
        }
        return ClassUtils.newObject(environment.parsing(confValue).toString());
    }

    private String[] listToArrayByStr(List<?> list){
        List<String> strList = list.stream().map(Object::toString).collect(Collectors.toList());
        return strList.toArray(new String[]{});
    }
}
