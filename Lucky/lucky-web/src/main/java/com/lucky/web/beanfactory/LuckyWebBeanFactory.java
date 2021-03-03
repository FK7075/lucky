package com.lucky.web.beanfactory;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.container.FusionStrategy;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.web.annotation.CallController;
import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.ControllerAdvice;
import com.lucky.web.annotation.RestController;
import com.lucky.web.httpclient.callcontroller.CallControllerProxy;
import com.lucky.web.interceptor.HandlerInterceptor;
import com.lucky.web.interceptor.Interceptor;
import com.lucky.web.interceptor.InterceptorRegistry;
import com.lucky.web.interceptor.PathAndInterceptor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/23 16:25
 */
public class LuckyWebBeanFactory extends IOCBeanFactory {

    public LuckyWebBeanFactory(){
        super();
    }

    public LuckyWebBeanFactory(FusionStrategy fusionStrategy){
        super(fusionStrategy);
    }

    private static final Class<? extends Annotation>[] CONTROLLER_ANNOTATION=
            new Class[]{Controller.class,CallController.class, RestController.class, ControllerAdvice.class};

    @Override
    public List<Module> createBean() {
        List<Module> modules=new ArrayList<>();
        List<Class<?>> controllerClasses = getPluginByAnnotation(CONTROLLER_ANNOTATION);
        for (Class<?> controllerClass : controllerClasses) {
            String beanName = getBeanName(controllerClass);
            String beanType = getBeanType(controllerClass);
            lifecycleMange.beforeCreatingInstance(controllerClass,beanName,beanType);
            if(AnnotationUtils.strengthenIsExist(controllerClass,Controller.class)){
                modules.add(new Module(beanName,beanType,ClassUtils.newObject(controllerClass)));
            }else{
                modules.add(new Module(beanName,beanType,CallControllerProxy.getCallControllerProxyObject(controllerClass)));
            }
        }
        List<Class<?>> interceptors = getPluginByAnnotation(Interceptor.class);
        for (Class<?> interceptorClass : interceptors) {
            if(!HandlerInterceptor.class.isAssignableFrom(interceptorClass)){
                throw new RuntimeException("拦截器注册失败！错误的类型 `"+interceptorClass+"`,拦截器必须是 `com.lucky.web.interceptor.HandlerInterceptor` 的子类！");
            }
            PathAndInterceptor pathAndInterceptor=new PathAndInterceptor();
            Interceptor annotation = interceptorClass.getAnnotation(Interceptor.class);
            pathAndInterceptor.setInterceptor((HandlerInterceptor)ClassUtils.newObject(interceptorClass));
            pathAndInterceptor.setPath(annotation.value());
            pathAndInterceptor.setExcludePath(annotation.excludePath());
            pathAndInterceptor.setPriority(annotation.priority());
            InterceptorRegistry.addHandlerInterceptor(pathAndInterceptor);
        }

        return modules;
    }

    public String getBeanName(Class<?> aClass) {
        Annotation controllerAnnotation = AnnotationUtils.getByArray(aClass, CONTROLLER_ANNOTATION);
        String id= (String) AnnotationUtils.getValue(controllerAnnotation,"id");
        return Assert.isBlankString(id)? Namer.getBeanName(aClass):id;
    }

    @Override
    public String getBeanType(Class<?> aClass) {
        return AnnotationUtils.strengthenGet(aClass, Component.class).get(0).type();
    }
}
