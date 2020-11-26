package com.lucky.framework.container;

import com.lucky.framework.container.factory.Namer;
import com.lucky.framework.exception.AutowiredException;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.base.BaseUtils;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.framework.uitls.reflect.FieldUtils;
import com.lucky.framework.annotation.Autowired;
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
public abstract class Injection implements Namer {

    private static Logger log= LogManager.getLogger(Injection.class);
    private static SingletonContainer singletonPool= RegisterMachine.getRegisterMachine().getSingletonPool();

    public static void setSingletonPool(SingletonContainer singletonPool) {
        Injection.singletonPool = singletonPool;
    }

    public Injection(){
        Module module=
                new Module(getBeanName(getClass()),getBeanType(getClass()),this);
        Injection.injection(module);
    }

    @Override
    public String getBeanName(Class<?> aClass){
        return BaseUtils.lowercaseFirstLetter(aClass.getSimpleName());
    }

    @Override
    public String getBeanType(Class<?> aClass){
        return "component";
    }

    public static void injection(Module mod){
        Object bean=mod.getComponent();
        Class<?> beanClass=bean.getClass();
        List<Field> fields = ClassUtils.getFieldByAnnotation(beanClass, Autowired.class);
        for (Field field : fields) {
            Autowired autowired= AnnotationUtils.get(field,Autowired.class);
            String value = autowired.value();
            if(!Assert.isBlankString(value)){
                Module module = singletonPool.getBean(value);
                if(module==null){
                    AutowiredException lex=new AutowiredException("无法为 【组件ID："+mod.getId()+"】\""+beanClass+"\" 注入【属性名称："+field.getName()+"】ID为 \""+value+"\" 的属性，因为在IOC容器中没有找到ID为\""+value+"\"的组件！");
                    log.error(lex);
                    throw lex;
                }
                FieldUtils.setValue(bean,field,singletonPool.getBean(value).getComponent());
           }else{
                List<Module> modules = singletonPool.getBeanByClass(field.getType());
                if(Assert.isEmptyCollection(modules)){
                    AutowiredException lex=new AutowiredException("无法为【组件ID："+mod.getId()+"】\""+beanClass+"\" 注入【属性名称："+field.getName()+"】类型为 \""+field.getType()+"\" 的属性，因为在IOC容器中没有找到该类型的组件！");
                    log.error(lex);
                    throw lex;
                }else if(modules.size()!=1){
                    AutowiredException lex=new AutowiredException("无法为【组件ID："+mod.getId()+"】\""+beanClass+"\" 注入【属性名称："+field.getName()+"】类型为 \""+field.getType()+"\" 的属性，因为在IOC容器中存在多个该类型的组件！建议您使用@Autowired注解的value属性来指定该属性组件的ID");
                    log.error(lex);
                    throw lex;
                }else{
                    FieldUtils.setValue(bean,field,modules.get(0).getComponent());
                }
            }
        }
    }

    public static void injection(Object bean,String beanType){
        String beanId=BaseUtils.lowercaseFirstLetter(bean.getClass().getSimpleName());
        Module module=new Module(beanId,beanType,bean);
        injection(module);
    }

}
