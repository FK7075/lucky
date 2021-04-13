package org.luckyframework.beans;

import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.annotation.Autowired;
import org.luckyframework.beans.annotation.Primary;
import org.luckyframework.beans.annotation.Qualifier;
import org.luckyframework.beans.factory.BeanFactory;
import org.luckyframework.beans.factory.ListableBeanFactory;
import org.luckyframework.exception.NoSuchBeanDefinitionException;
import org.luckyframework.exception.NoUniqueBeanDefinitionException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基于@Autowired注解的属性注入
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 14:52
 */
public class AutowiredInjection implements Injection {
    @Override
    public void injection(Object instance, ListableBeanFactory beanFactory) {
        Class<?> beanClass = instance.getClass();
        List<Field> autowiredFields = ClassUtils.getFieldByAnnotation(beanClass, Autowired.class);
        for (Field field : autowiredFields) {
            Class<?> fieldType = field.getType();
            Autowired autowired = field.getAnnotation(Autowired.class);
            String[] forType = beanFactory.getBeanNamesForType(fieldType);
            if(Assert.isEmptyArray(forType)){
                if (autowired.required()){
                    throw new NoSuchBeanDefinitionException(fieldType);
                }
            }
            int size = forType.length;

            // 类型匹配，匹配到唯一一个Bean实例
            if(size == 1){
                FieldUtils.setValue(instance,field,beanFactory.getBean(forType[0]));
                continue;
            }

            // 匹配到多个Bean实例，而且属性上没有标注@Qualifier
            Qualifier qualifier = field.getAnnotation(Qualifier.class);
            if(qualifier == null){
                List<String> primaryNames = new ArrayList<>();
                for (String type : forType) {
                    Class<?> beanType = beanFactory.getType(type);
                    if(beanType.isAnnotationPresent(Primary.class)){
                        primaryNames.add(type);
                    }
                }
                // 配置了优先级
                if(primaryNames.size() == 1){
                    FieldUtils.setValue(instance,field,beanFactory.getBean(primaryNames.get(0)));
                    continue;
                }
                throw new NoUniqueBeanDefinitionException(ResolvableType.forType(fieldType),forType);
            }

            // 匹配到多个Bean实例，而且属性被@Qualifier标注
            String value = qualifier.value();
            if(Arrays.asList(forType).contains(value)){
                FieldUtils.setValue(instance,field,beanFactory.getBean(value));
                continue;
            }
            throw new NoUniqueBeanDefinitionException(ResolvableType.forType(fieldType),forType);
        }
    }
}
