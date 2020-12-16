package com.lucky.framework.container.factory;

import com.lucky.framework.annotation.Bean;
import com.lucky.framework.container.Injection;
import com.lucky.framework.container.Module;
import com.lucky.framework.proxy.ASMUtil;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.base.BaseUtils;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.framework.uitls.reflect.MethodUtils;
import com.lucky.framework.uitls.reflect.ParameterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午12:50
 */
public class ConfigurationBeanFactory extends IOCBeanFactory{

    private static final Logger log= LoggerFactory.getLogger(AnnotationUtils.class);
    private List<Module> configurationBeans;

    public ConfigurationBeanFactory(List<Module> configurationBeans) {
        this.configurationBeans = configurationBeans;
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
            ClassUtils.getMethodByAnnotation(confClass, Bean.class)
                    .stream().filter((m)-> MethodUtils.getParameter(m).length == 0)
                    .forEach((method)-> {
                        Object invoke = MethodUtils.invoke(conf, method);
                        if(Assert.isNotNull(invoke)){
                            Module mod=new Module(getBeanId(confClass,method),getBeanType(method),invoke);
                            list.add(mod);
                        }
                    });
        }

        //执行@Configuration 的有参@Bean方法，并收集所有非空返回结果（等待注册）
        for (Object conf : configBeans) {
            Class<?> confClass = conf.getClass();
            List<Method> haveParamBeanMethods = ClassUtils.getMethodByAnnotation(confClass, Bean.class)
                    .stream().filter((m) -> MethodUtils.getParameter(m).length != 0)
                    .collect(Collectors.toList());
            for (Method beanMethod : haveParamBeanMethods) {
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
                Object invoke=MethodUtils.invoke(conf,beanMethod,params);
                list.add(new Module(beanId,beanType,invoke));
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
