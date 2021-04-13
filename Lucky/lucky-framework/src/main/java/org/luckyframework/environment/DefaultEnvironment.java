package org.luckyframework.environment;

import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;
import com.lucky.utils.base.StringUtils;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.Value;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.jexl.JexlEngineUtil;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import org.luckyframework.beans.annotation.PropertySource;
import org.luckyframework.beans.annotation.PropertySourceReader;


import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/25 0025 14:24
 */
public class DefaultEnvironment implements Environment {

    private final JexlEngineUtil jexlEngineUtil;
    private final Map<String,Object> environmentMap = new ConcurrentHashMap<>(225);

    public DefaultEnvironment(){
        this(new String[]{},"UTF-8",false);
    }

    public DefaultEnvironment(String ...locationPattern){
        this(locationPattern,"UTF-8",false);
    }

    public DefaultEnvironment(PropertySource propertySource){
        this(propertySource.value(),propertySource.encoding(), propertySource.ignoreResourceNotFound());
    }

    public DefaultEnvironment(String[] locationPatterns, String encoding, boolean ignoreResourceNotFound){
        PropertySourceReader psr = new PropertySourceReader(locationPatterns,encoding,ignoreResourceNotFound);
        defaultInit();
        environmentMap.putAll(psr.getResourceData());
        jexlEngineUtil = new JexlEngineUtil(environmentMap);
    }

    public DefaultEnvironment(PropertySource...propertySource){
        defaultInit();
        for (PropertySource source : propertySource) {
            environmentMap.putAll(new PropertySourceReader(source).getResourceData());
        }
        jexlEngineUtil = new JexlEngineUtil(environmentMap);
    }


    private void defaultInit(){
        environmentMap.putAll(System.getenv());
        Properties properties = System.getProperties();
        for (Map.Entry<Object,Object> e : properties.entrySet()){
            environmentMap.put(e.getKey().toString(),e.getValue());
        }

        YamlConfAnalysis yaml = ConfigUtils.getYamlConfAnalysis();
        if(yaml != null){
            environmentMap.putAll(yaml.getMap());
        }
    }

    private Object getObject(String key){
        Object value = jexlEngineUtil.getProperties(key);
        return value == null ? key :value;
    }

    private Object getProperties(String prefix){
        return getObject("${"+prefix+"}");
    }

    @Override
    public String getSystemEvn(String key) {
        return System.getenv(key);
    }

    @Override
    public String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    @Override
    public Object getDefaultConfigFileProperty(String key) {
        return null;
    }

    @Override
    public Object getExternalConfigFileProperty(String key) {
        return null;
    }

    @Override
    public Object parsing(Object expression) {
        if(expression instanceof String){
            return getObject(expression.toString());
        }
        return expression;
    }

    @Override
    public Object getProperty(String key) {
        return getProperties(key);
    }

    public static void valueInjection(Object bean, YamlConfAnalysis yaml, List<Field> valueFields, String prefix, String delimiter){
        if(yaml==null){
            return;
        }
        if(Assert.isEmptyCollection(valueFields)){
            return;
        }
        String exp;
        Class<?> fieldType;
        for (Field valueField : valueFields) {
            exp=getExpression(valueField,prefix,delimiter);
            Object confValue = yaml.getObject(exp);
            if(confValue==null){
                continue;
            }
            fieldType = valueField.getType();
            //Class类型
            if(Class.class==fieldType){
                setField(bean,valueField, ClassUtils.getClass(confValue.toString()));
                continue;
            }
            //基本类型以及基本类型的包装类型
            if(ClassUtils.isPrimitive(fieldType)||ClassUtils.isSimple(fieldType)){
                setField(bean,valueField, JavaConversion.strToBasic(confValue.toString(), fieldType));
                continue;
            }

            //基本类型以及其包装类型的数组
            if(ClassUtils.isSimpleArray(fieldType)){
                List<String> confList= (List<String>) confValue;
                setField(bean,valueField,JavaConversion.strArrToBasicArr(listToArrayByStr(confList), fieldType));
                continue;
            }

            //非JDK类型
            if(!ClassUtils.isJdkType(fieldType)){
                if(JexlEngineUtil.isExpression(exp)){
                    setField(bean,valueField,getFieldObject(yaml,valueField,fieldType,exp,delimiter));
                }
                continue;
            }


            //集合类型
            if(Collection.class.isAssignableFrom(fieldType)){
                Class<?> genericType = ClassUtils.getGenericType(valueField.getGenericType())[0];

                //泛型为基本类型
                if(ClassUtils.isSimple(genericType)){
                    List<String> confList= (List<String>) confValue;
                    String[] confArr=listToArrayByStr(confList);
                    if(List.class.isAssignableFrom(fieldType)){
                        setField(bean,valueField, Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toList()));
                        continue;
                    }
                    if(Set.class.isAssignableFrom(fieldType)){
                        setField(bean,valueField, Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toSet()));
                        continue;
                    }
                    continue;
                }

                //泛型为非JDK类型
                if(!ClassUtils.isJdkType(genericType)){
                    if(!JexlEngineUtil.isExpression(exp)){
                        continue;
                    }
                    List<Object> confList= (List<Object>) confValue;
                    List<Object> confValueList=new ArrayList<>(confList.size());
                    String listExp=exp.substring(2,exp.length()-1).trim();
                    for(int i=0,j=confList.size();i<j;i++){
                        String pre=listExp+".get("+i+")";
                        confValueList.add(getFieldObject(yaml,valueField,genericType,pre,delimiter));
                    }
                    setField(bean,valueField,confValueList);
                    continue;
                }

                //泛型为Class
                if(Class.class==genericType){
                    List<String> confList= (List<String>) confValue;
                    Class<?>[] classes=new Class[confList.size()];
                    for (int i = 0,j=classes.length; i < j; i++) {
                        classes[i]=ClassUtils.getClass(yaml.getObject(confList.get(i)).toString());
                    }
                    if(List.class.isAssignableFrom(fieldType)){
                        setField(bean,valueField, Stream.of(classes).collect(Collectors.toList()));
                        continue;
                    }
                    if(Set.class.isAssignableFrom(fieldType)){
                        setField(bean,valueField, Stream.of(classes).collect(Collectors.toSet()));
                        continue;
                    }
                    continue;
                }
            }

            if(Map.class.isAssignableFrom(fieldType)){
                Map<String,Object> $confMap= (Map<String, Object>) confValue;
                Map<String,Object> confMap=new HashMap<>();
                for(Map.Entry<String,Object> entry:$confMap.entrySet()){
                    confMap.put(entry.getKey(),yaml.getObject(entry.getValue()));
                }
                setField(bean,valueField,confMap);
                continue;
            }
            setField(bean,valueField,ClassUtils.newObject(yaml.getObject(confValue).toString()));
        }
    }

    private static Object getFieldObject(YamlConfAnalysis yaml,Field field,Class<?> fieldType,String exp,String delimiter){
        Object fieldObject = ClassUtils.newObject(fieldType);
        String fieldPrefix=exp;
        if(JexlEngineUtil.isExpression(exp)){
            fieldPrefix=exp.substring(2,exp.length()-1).trim();
        }
        Field[] fieldObjectFields = ClassUtils.getAllFields(fieldType);
        Value valueAnn = field.getAnnotation(Value.class);
        if(valueAnn!=null && !Assert.isBlankString(valueAnn.humpConversion())){
            delimiter=valueAnn.humpConversion();
        }
        valueInjection(fieldObject,yaml, ArrayUtils.arrayToList(fieldObjectFields),fieldPrefix,delimiter);
        return fieldObject;
    }

    private static String getExpression(Field field,String prefix,String delimiter){
        String exp = getExp(field, delimiter);
        if(!JexlEngineUtil.isExpression(exp)||"".equals(prefix)){
            return exp;
        }
        return "${"+prefix+"."+exp.substring(2,exp.length()-1).trim()+"}";
    }

    private static String getExp(Field field,String delimiter){
        Value valueAnn = field.getAnnotation(Value.class);
        if(valueAnn==null){
            return "${"+ StringUtils.humpToLine(field.getName(),delimiter)+"}";
        }
        String value = valueAnn.value();
        if(!"".equals(value)){
            return value;
        }
        String humpConversion = valueAnn.humpConversion();
        delimiter="".equals(humpConversion)?delimiter:humpConversion;
        return "${"+StringUtils.humpToLine(field.getName(),delimiter)+"}";
    }

    private static void setField(Object bean,Field field,Object value){
        FieldUtils.setValue(bean, field, value);
    }

    private static String[] listToArrayByStr(List<String> list){
        String[] array=new String[list.size()];
        list.toArray(array);
        return array;
    }
}
