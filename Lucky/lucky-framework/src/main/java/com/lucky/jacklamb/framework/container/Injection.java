package com.lucky.jacklamb.framework.container;

import com.lucky.jacklamb.framework.annotation.Autowired;
import com.lucky.jacklamb.framework.exception.AutowiredException;
import com.lucky.jacklamb.framework.uitls.base.Assert;
import com.lucky.jacklamb.framework.uitls.reflect.AnnotationUtils;
import com.lucky.jacklamb.framework.uitls.reflect.ClassUtils;
import com.lucky.jacklamb.framework.uitls.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午3:58
 */
public abstract class Injection {

    private static final Logger log= LogManager.getLogger(Injection.class);
    private static final SingletonContainer singletonPool= RegisterMachine.getRegisterMachine().getSingletonPool();

    public Injection(){
        Injection.injection(this);
    }

    public static void injection(Object bean){
        Class<?> beanClass=bean.getClass();
        List<Field> fields = ClassUtils.getFieldByAnnotation(beanClass, Autowired.class);
        for (Field field : fields) {
            Autowired autowired= AnnotationUtils.get(field,Autowired.class);
            String value = autowired.value();
            if(Assert.isBlankString(value)){
                Module module = singletonPool.getBean(value);
                if(module==null){
                    AutowiredException lex=new AutowiredException("无法为\""+beanClass+"\"注入ID为\""+value+"\"的属性，因为在IOC容器中没有找到ID为"+value+"的组件！");
                    log.error(lex);
                    throw lex;
                }
                FieldUtils.setValue(bean,field,singletonPool.getBean(value).getComponent());
           }else{
                List<Module> modules = singletonPool.getBeanByClass(field.getType());
                if(Assert.isEmptyCollection(modules)){
                    AutowiredException lex=new AutowiredException("无法为\""+beanClass+"\"注入类型为\""+field.getType()+"\"的属性，因为在IOC容器中没有找到该类型的组件！");
                    log.error(lex);
                    throw lex;
                }else if(modules.size()!=1){
                    AutowiredException lex=new AutowiredException("无法为\""+beanClass+"\"注入类型为\""+field.getType()+"\"的属性，因为在IOC容器中存在多个该类型的组件！，建议您使用@Autowired注解的value属性指定该组件的ID");
                    log.error(lex);
                    throw lex;
                }else{
                    FieldUtils.setValue(bean,field,modules.get(0).getComponent());
                }
            }
        }
    }

}
