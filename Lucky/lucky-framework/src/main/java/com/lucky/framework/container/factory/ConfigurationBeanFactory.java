package com.lucky.framework.container.factory;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.annotation.Configuration;
import com.lucky.framework.container.Injection;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.lifecycle.ContainerLifecycleMange;
import com.lucky.utils.base.Assert;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.proxy.ASMUtil;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.MethodUtils;
import com.lucky.utils.reflect.ParameterUtils;
import com.lucky.utils.type.ResolvableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午12:50
 */
public class ConfigurationBeanFactory extends IOCBeanFactory{

    private static final Logger log= LoggerFactory.getLogger(ConfigurationBeanFactory.class);
    private final List<Module> configurationBeans;

    public ConfigurationBeanFactory(List<Module> configurationBeans) {
        super();
        this.configurationBeans = configurationBeans
                .stream()
                .sorted(Comparator.comparing(m->AnnotationUtils.strengthenGet(m.getOriginalType(), Configuration.class).get(0).priority()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Module> createBean() {
        List<Module> list=new ArrayList<>(50);
        List<Object> configBeans = configurationBeans.stream().map((m) -> {
            Injection.injection(m);
            return m.getComponent();
        }).collect(Collectors.toList());

        //执行@Configuration 的无参@Bean方法，并收集所有非空返回结果（等待注册）
        for (Object conf : configBeans) {
            Class<?> confClass=conf.getClass();
            List<Method> collect = ClassUtils.getMethodByAnnotation(confClass, Bean.class)
                    .stream().filter((m) -> MethodUtils.getParameter(m).length == 0)
                    .sorted(Comparator.comparing(m -> AnnotationUtils.strengthenGet(m, Bean.class).get(0).priority()))
                    .collect(Collectors.toList());
            for (Method method : collect) {
                Class<?> returnType = method.getReturnType();
                String beanName = getBeanId(confClass, method);
                String beanType = getBeanType(method);
                if(returnType!=void.class){
                    lifecycleMange.beforeCreatingInstance(returnType,beanName,beanType);
                }
                Object invoke = MethodUtils.invoke(conf, method);
                if(Assert.isNotNull(invoke)){
                    Module mod=new Module(beanName,beanType,invoke,ResolvableType.forMethodReturnType(method,confClass));
                    list.add(mod);
                }
            }
        }

        //执行@Configuration 的有参@Bean方法，并收集所有非空返回结果（等待注册）
        for (Object conf : configBeans) {
            Class<?> confClass = conf.getClass();
            List<Method> haveParamBeanMethods = ClassUtils.getMethodByAnnotation(confClass, Bean.class)
                    .stream().filter((m) -> MethodUtils.getParameter(m).length != 0)
                    .sorted(Comparator.comparing(m->AnnotationUtils.strengthenGet(m,Bean.class).get(0).priority()))
                    .collect(Collectors.toList());
            for (Method beanMethod : haveParamBeanMethods) {
                Class<?> returnType = beanMethod.getReturnType();
                String beanId=getBeanId(confClass,beanMethod);
                String beanType=getBeanType(beanMethod);
                Parameter[] parameter = MethodUtils.getParameter(beanMethod);
                String[] paramNames = ASMUtil.getMethodParamNames(beanMethod);
                Object[] params=new Object[parameter.length];
                for (int i = 0,j=parameter.length; i < j; i++) {
                    List<Module> beans = getBeanByClass(parameter[i].getType());
                    if(beans.isEmpty()){
                        params[i]=null;
                        log.warn("无法执行的@Bean方法，参数注入注入失败，在IOC容器中找不到类型为--"+parameter[i].getType()+"--的Bean，警告位置："+beanMethod);
                    }else if(beans.size()==1){
                        params[i]=beans.get(0).getComponent();
                    }else{
                        String iocId= ParameterUtils.getParamName(parameter[i],paramNames[i]);
                        if(!isIOCId(iocId)){
                            params[i]=null;
                            log.warn("无法执行的@Bean方法，参数注入注入失败，在IOC容器中找不到ID为--"+iocId+"--的Bean，警告位置："+beanMethod);
                        }else{
                            params[i]=getBean(iocId).getComponent();
                        }
                    }
                }
                if(returnType!=void.class){
                    lifecycleMange.beforeCreatingInstance(returnType,beanId,beanType);
                }
                Object invoke=MethodUtils.invoke(conf,beanMethod,params);
                if(Assert.isNotNull(invoke)){
                    list.add(new Module(beanId,beanType,invoke, ResolvableType.forMethodReturnType(beanMethod,confClass)));
                }
            }
        }

        return list;
    }

    private String getBeanId(Class<?> confClass,Method beanMethod){
        String beanValue= AnnotationUtils.get(beanMethod,Bean.class).value();
        return Assert.isBlankString(beanValue)
                ? BaseUtils.lowercaseFirstLetter(confClass.getSimpleName() + "_" + beanMethod.getName())
                : beanValue;
    }

    private String getBeanType(Method beanMethod){
        return AnnotationUtils.get(beanMethod,Bean.class).type();
    }

}
