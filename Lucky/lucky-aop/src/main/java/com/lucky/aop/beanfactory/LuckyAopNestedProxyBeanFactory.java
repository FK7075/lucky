package com.lucky.aop.beanfactory;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.container.FusionStrategy;
import com.lucky.framework.container.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 创建Lucky AOP代理对象的BeanFactory
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/28 上午4:14
 */
@Component
public class LuckyAopNestedProxyBeanFactory extends BaseAopBeanFactory {

    public LuckyAopNestedProxyBeanFactory(){
        super();
    }

    public LuckyAopNestedProxyBeanFactory(FusionStrategy fusionStrategy){
        super(fusionStrategy);
    }


    @Override
    public List<Module> createBean() {
        loadPoint();
        proxy(getBeans(),false);
        return super.createBean();
    }


}
