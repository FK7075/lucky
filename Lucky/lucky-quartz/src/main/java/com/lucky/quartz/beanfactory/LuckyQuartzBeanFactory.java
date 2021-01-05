package com.lucky.quartz.beanfactory;

import com.lucky.framework.container.FusionStrategy;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.quartz.annotation.QuartzJobs;
import com.lucky.quartz.proxy.QuartzProxy;
import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.AnnotationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/9 上午2:14
 */
public class LuckyQuartzBeanFactory extends IOCBeanFactory {


    public LuckyQuartzBeanFactory() {
    }

    public LuckyQuartzBeanFactory(FusionStrategy fusionStrategy) {
        super(fusionStrategy);
    }

    @Override
    public Map<String, Module> replaceBean() {
        return super.replaceBean();
    }

    @Override
    public List<Module> createBean() {
        List<Module> quartz=new ArrayList<>();
        List<Class<?>> quartzJobClasses = getPluginByAnnotation(QuartzJobs.class);
        for (Class<?> quartzJobClass : quartzJobClasses) {
            quartz.add(new Module(getBeanName(quartzJobClass),"quartz_job",QuartzProxy.getProxy(quartzJobClass)));
        }
        return quartz;
    }

    public String getBeanName(Class<?> aClass) {
        QuartzJobs quartzJobs = AnnotationUtils.get(aClass, QuartzJobs.class);
        String id = quartzJobs.value();
        return Assert.isBlankString(id)? Namer.getBeanName(aClass):id;
    }
}
