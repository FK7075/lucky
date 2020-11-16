package com.lucky.framework.container.factory;

import com.lucky.framework.annotation.*;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.base.BaseUtils;
import com.lucky.framework.uitls.reflect.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 默认的起名器
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/15 下午11:50
 */
public class BeanNamer implements Namer {

    private Class<? extends Annotation>[] COMPONENT_ANNOTATION=
            new Class[]{Component.class, Controller.class, Repository.class, Service.class, Configuration.class};

    @Override
    public String getBeanName(Class<?> beanClass) {
        Annotation annotation = AnnotationUtils.getByArray(beanClass, COMPONENT_ANNOTATION);
        String id = (String) AnnotationUtils.getValue(annotation, "id");
        String value= (String) AnnotationUtils.getValue(annotation,"value");
        if(!Assert.isBlankString(id)){
            return id;
        }
        if(!Assert.isBlankString(value)){
            return value;
        }
        return BaseUtils.lowercaseFirstLetter(beanClass.getSimpleName());
    }

    @Override
    public String getBeanType(Class<?> beanClass) {
        List<Component> components = AnnotationUtils.strengthenGet(beanClass, Component.class);
        if(Assert.isEmptyCollection(components)){

        }
        if(components.size()!=1){

        }
        return components.get(0).type();
    }
}
