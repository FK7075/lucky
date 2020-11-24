package com.lucky.web.beanfactory;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.web.annotation.CallController;
import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.ControllerAdvice;
import com.lucky.web.annotation.RestController;
import com.lucky.web.httpclient.callcontroller.CallControllerProxy;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/23 16:25
 */
public class LuckyWebBeanFactory extends IOCBeanFactory {

    private static final Class<? extends Annotation>[] CONTROLLER_ANNOTATION=
            new Class[]{Controller.class,CallController.class, RestController.class, ControllerAdvice.class};

    @Override
    public List<Module> createBean() {
        List<Module> modules=new ArrayList<>();
        List<Class<?>> controllerClasses = getPluginByAnnotation(CONTROLLER_ANNOTATION);
        for (Class<?> controllerClass : controllerClasses) {
            if(AnnotationUtils.strengthenIsExist(controllerClass,Controller.class)){
                modules.add(new Module(getBeanName(controllerClass),
                                   getBeanType(controllerClass),
                                   ClassUtils.newObject(controllerClass)));
            }else{
                modules.add(new Module(getBeanName(controllerClass),
                                getBeanType(controllerClass),
                                CallControllerProxy.getCallControllerProxyObject(controllerClass)));
            }
        }
        return modules;
    }

    @Override
    public String getBeanName(Class<?> aClass) {
        Annotation controllerAnnotation = AnnotationUtils.getByArray(aClass, CONTROLLER_ANNOTATION);
        String id= (String) AnnotationUtils.getValue(controllerAnnotation,"id");
        return Assert.isBlankString(id)?super.getBeanName(aClass):id;
    }

    @Override
    public String getBeanType(Class<?> aClass) {
        return AnnotationUtils.strengthenGet(aClass, Component.class).get(0).type();
    }
}
