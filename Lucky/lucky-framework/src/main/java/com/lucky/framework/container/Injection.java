package com.lucky.framework.container;

import com.lucky.framework.annotation.Autowired;
import com.lucky.framework.container.factory.Namer;
import com.lucky.framework.exception.AutowiredException;
import com.lucky.utils.annotation.ConfigurationProperties;
import com.lucky.utils.annotation.PropertySource;
import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;
import com.lucky.utils.base.StringUtils;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.Value;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.file.*;
import com.lucky.utils.jexl.JexlEngineUtil;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午3:58
 */
public abstract class Injection implements Namer {

    private static final Logger log= LoggerFactory.getLogger(Injection.class);
    private static SingletonContainer singletonPool= RegisterMachine.getRegisterMachine().getSingletonPool();

    public static void setSingletonPool(SingletonContainer singletonPool) {
        Injection.singletonPool = singletonPool;
    }

    public Injection(){
        Module module=
                new Module(Namer.getBeanName(getClass()),getBeanType(getClass()),this);
        Injection.injection(module);
    }

    @Override
    public String getBeanType(Class<?> aClass){
        return "component";
    }

    public static void injection(Module mod){
        if(mod.isInjection()){
            return;
        }
        Object bean=mod.getComponent();
        Class<?> beanClass=mod.getOriginalType();
        String beanName=beanClass.getName();
        List<Field> fields= ClassUtils.getFieldByStrengthenAnnotation(beanClass, Autowired.class);
        for (Field field : fields) {
            Autowired autowired= AnnotationUtils.strengthenGet(field,Autowired.class).get(0);
            String value = autowired.value();

            //ID注入
            if(!Assert.isBlankString(value)){
                Module module = singletonPool.getBean(value);
                if(module==null){
                    AutowiredException lex=new AutowiredException("无法为 【组件ID："+mod.getId()+"】\""+beanClass+"\" 注入【属性名称："+field.getName()+"】ID为 \""+value+"\" 的属性，因为在IOC容器中没有找到ID为\""+value+"\"的组件！");
                    log.error("AutowiredException",lex);
                    throw lex;
                }
                Object component = singletonPool.getBean(value).getComponent();
                FieldUtils.setValue(bean,field,component);
                log.debug("Attribute injection [BY-ID] `"+beanName+"`「"+field.getName()+"」 <= "+component);
           }
            //类型注入
            else{
                Class<?> fieldType = field.getType();
                List<Module> modules = singletonPool.getBeanByClass(fieldType);
                if(Assert.isEmptyCollection(modules)){
                    AutowiredException lex=new AutowiredException("无法为【组件ID："+mod.getId()+"】\""+beanClass+"\" 注入【属性名称："+field.getName()+"】类型为 \""+fieldType+"\" 的属性，因为在IOC容器中没有找到该类型的组件！");
                    log.error("AutowiredException",lex);
                    throw lex;
                }else if(modules.size()!=1){
                    Module beanByField = singletonPool.getBeanByField(beanClass, fieldType);
                    if(beanByField!=null){
                        FieldUtils.setValue(bean,field,beanByField.getComponent());
                        continue;
                    }
                    AutowiredException lex=new AutowiredException("无法为【组件ID："+mod.getId()+"】\""+beanClass+"\" 注入【属性名称："+field.getName()+"】类型为 \""+field.getType()+"\" 的属性，因为在IOC容器中存在多个该类型的组件！建议您使用@Autowired注解的value属性来指定该属性组件的ID");
                    log.error("AutowiredException",lex);
                    throw lex;
                }else{
                    Object component = modules.get(0).getComponent();
                    FieldUtils.setValue(bean,field,component);
                    log.debug("Attribute injection [BY-CLASS] `"+beanName+"`「"+field.getName()+"」 <= "+component);

                }
            }
        }
        List<Field> fieldList = ArrayUtils.arrayToList(ClassUtils.getAllFields(beanClass))
                .stream().filter((c)-> !Modifier.isFinal(c.getModifiers())&&!c.isAnnotationPresent(Autowired.class)
                ).collect(Collectors.toList());
        mod.setInjection(true);
        PropertySource psAnn = beanClass.getAnnotation(PropertySource.class);
        if(psAnn!=null){
            List<BufferedReader> readers = Stream.of(psAnn.value()).map(Resources::getReader).collect(Collectors.toList());
            YamlConfAnalysis yaml=new YamlConfAnalysis(readers);
            String delimiter = psAnn.humpConversion();
            String prefix = psAnn.prefix();
            valueInjection(bean,yaml,fieldList,prefix,delimiter);
            return;
        }
        ConfigurationProperties cpAnn = beanClass.getAnnotation(ConfigurationProperties.class);
        if(cpAnn!=null){
            YamlConfAnalysis yaml=ConfigUtils.getYamlConfAnalysis();
            String delimiter = cpAnn.humpConversion();
            String prefix = cpAnn.prefix();
            valueInjection(bean,yaml,fieldList,prefix,delimiter);
            return;
        }
        YamlConfAnalysis yaml=ConfigUtils.getYamlConfAnalysis();
        fieldList=ClassUtils.getFieldByStrengthenAnnotation(beanClass, Value.class);
        valueInjection(bean,yaml,fieldList,"","");
    }

    public static void injection(Object bean,String beanType){
        Module module=new Module(Namer.getBeanName(bean.getClass()),beanType,bean);
        injection(module);
    }

    public static void valueInjection(Object bean,YamlConfAnalysis yaml,List<Field> valueFields,String prefix,String delimiter){
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
                FieldUtils.setValue(bean,valueField,ClassUtils.getClass(confValue.toString()));
                continue;
            }
            //基本类型以及基本类型的包装类型
            if(ClassUtils.isPrimitive(fieldType)||ClassUtils.isSimple(fieldType)){
                FieldUtils.setValue(bean,valueField, JavaConversion.strToBasic(confValue.toString(),fieldType));
                continue;
            }

            //基本类型以及其包装类型的数组
            if(ClassUtils.isSimpleArray(fieldType)){
                List<String> confList= (List<String>) confValue;
                FieldUtils.setValue(bean,valueField,JavaConversion.strArrToBasicArr(listToArrayByStr(confList),fieldType));
                continue;
            }

            //非JDK类型
            if(!ClassUtils.isJdkType(fieldType)){
                if(JexlEngineUtil.isExpression(exp)){
                    FieldUtils.setValue(bean,valueField,getFieldObject(yaml,valueField,fieldType,exp,delimiter));
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
                        FieldUtils.setValue(bean,valueField, Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toList()));
                        continue;
                    }
                    if(Set.class.isAssignableFrom(fieldType)){
                        FieldUtils.setValue(bean,valueField, Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toSet()));
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
                    FieldUtils.setValue(bean,valueField,confValueList);
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
                        FieldUtils.setValue(bean,valueField, Stream.of(classes).collect(Collectors.toList()));
                        continue;
                    }
                    if(Set.class.isAssignableFrom(fieldType)){
                        FieldUtils.setValue(bean,valueField, Stream.of(classes).collect(Collectors.toSet()));
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
                FieldUtils.setValue(bean,valueField,confMap);
                continue;
            }
            FieldUtils.setValue(bean,valueField,ClassUtils.newObject(yaml.getObject(confValue).toString()));
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
            return "${"+StringUtils.humpToLine(field.getName(),delimiter)+"}";
        }
        String value = valueAnn.value();
        if(!"".equals(value)){
            return value;
        }
        String humpConversion = valueAnn.humpConversion();
        delimiter="".equals(humpConversion)?delimiter:humpConversion;
        return "${"+StringUtils.humpToLine(field.getName(),delimiter)+"}";
    }

    private static String[] listToArrayByStr(List<String> list){
        String[] array=new String[list.size()];
        list.toArray(array);
        return array;
    }

}
