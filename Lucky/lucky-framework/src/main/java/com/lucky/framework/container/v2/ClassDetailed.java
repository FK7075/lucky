package com.lucky.framework.container.v2;

import com.lucky.framework.annotation.Autowired;
import com.lucky.framework.container.factory.BeanNamer;
import com.lucky.utils.config.Value;
import com.lucky.utils.reflect.ClassUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 组件Class的详细信息
 * @author fk
 * @version 1.0
 * @date 2021/3/3 0003 18:53
 */
public class ClassDetailed {

    private static final BeanNamer namer=new BeanNamer();

    private final String beanId;

    private final Class<?> beanClass;

    private final List<Field> autowiredFields;

    private final List<Field> valueField;

    private String scope;

    public ClassDetailed(Class<?> beanClass){
        this.beanClass=beanClass;
        this.beanId=namer.getBeanName(beanClass);
        this.valueField=ClassUtils.getFieldByAnnotation(beanClass, Value.class);
        this.autowiredFields = ClassUtils.getFieldByAnnotation(beanClass, Autowired.class);
    }

    public String getBeanId() {
        return beanId;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public List<Field> getAutowiredFields() {
        return autowiredFields;
    }

    public List<Field> getValueField() {
        return valueField;
    }
}
