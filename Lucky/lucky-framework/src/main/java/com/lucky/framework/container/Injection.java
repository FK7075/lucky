package com.lucky.framework.container;

import com.lucky.framework.annotation.Autowired;
import com.lucky.framework.container.factory.Namer;
import com.lucky.framework.exception.AutowiredException;
import com.lucky.utils.base.Assert;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.Value;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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
        List<Field> valueFields = ClassUtils.getFieldByStrengthenAnnotation(beanClass, Value.class);
        valueInjection(bean,valueFields);

        mod.setInjection(true);
    }

    public static void configurationPropertiesInjection(Object bean,Class<?> beanClass,String prefix){
        Field[] fields = ClassUtils.getAllFields(beanClass);
        for (Field field : fields) {

        }
    }

    public static void valueInjection(Object bean,List<Field> valueFiles){
        if(Assert.isEmptyCollection(valueFiles)){
            return;
        }
        YamlConfAnalysis yaml= ConfigUtils.getYamlConfAnalysis();
        String prefix;
        Class<?> fieldType;
        for (Field valueFile : valueFiles) {
            prefix=valueFile.getAnnotation(Value.class).value();
            Object confValue = yaml.getObject(prefix);
            if(confValue==null){
                continue;
            }
            fieldType = valueFile.getType();
            //Class类型
            if(Class.class==fieldType){
                FieldUtils.setValue(bean,valueFile,ClassUtils.getClass(confValue.toString()));
                continue;
            }
            //基本类型以及基本类型的包装类型
            if(ClassUtils.isPrimitive(fieldType)||ClassUtils.isSimple(fieldType)){
                FieldUtils.setValue(bean,valueFile, JavaConversion.strToBasic(confValue.toString(),fieldType));
                continue;
            }

            //基本类型以及其包装类型的数组
            if(ClassUtils.isSimpleArray(fieldType)){
                List<String> confList= (List<String>) confValue;
                FieldUtils.setValue(bean,valueFile,JavaConversion.strArrToBasicArr(listToArrayByStr(confList),fieldType));
                continue;
            }


            //集合类型
            if(Collection.class.isAssignableFrom(fieldType)){
                Class<?> genericType = ClassUtils.getGenericType(valueFile.getGenericType())[0];
                //泛型为基本类型
                List<String> confList= (List<String>) confValue;
                if(ClassUtils.isSimple(genericType)){
                    String[] confArr=listToArrayByStr(confList);
                    if(List.class.isAssignableFrom(fieldType)){
                        FieldUtils.setValue(bean,valueFile, Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toList()));
                        continue;
                    }
                    if(Set.class.isAssignableFrom(fieldType)){
                        FieldUtils.setValue(bean,valueFile, Stream.of(JavaConversion.strArrToBasicArr(confArr,genericType)).collect(Collectors.toSet()));
                        continue;
                    }
                }
                //泛型为Class
                if(Class.class==genericType){
                    Class<?>[] classes=new Class[confList.size()];
                    for (int i = 0,j=classes.length; i < j; i++) {
                        classes[i]=ClassUtils.getClass(yaml.getObject(confList.get(i)).toString());
                    }
                    if(List.class.isAssignableFrom(fieldType)){
                        FieldUtils.setValue(bean,valueFile, Stream.of(classes).collect(Collectors.toList()));
                        continue;
                    }
                    if(Set.class.isAssignableFrom(fieldType)){
                        FieldUtils.setValue(bean,valueFile, Stream.of(classes).collect(Collectors.toSet()));
                        continue;
                    }
                }
            }

            if(Map.class.isAssignableFrom(fieldType)){
                Map<String,Object> $confMap= (Map<String, Object>) confValue;
                Map<String,Object> confMap=new HashMap<>();
                for(Map.Entry<String,Object> entry:$confMap.entrySet()){
                    confMap.put(entry.getKey(),yaml.getObject(entry.getValue()));
                }
                FieldUtils.setValue(bean,valueFile,confMap);
                continue;
            }
            FieldUtils.setValue(bean,valueFile,ClassUtils.newObject(yaml.getObject(confValue).toString()));
        }
    }

    public static void propertySourceInjection(){

    }


    public static void injection(Object bean,String beanType){
        Module module=new Module(Namer.getBeanName(bean.getClass()),beanType,bean);
        injection(module);
    }


    private static String[] listToArrayByStr(List<String> list){
        String[] array=new String[list.size()];
        list.toArray(array);
        return array;
    }

}
