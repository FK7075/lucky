package com.lucky.cloud.client.beanfactory;

import com.lucky.cloud.client.annotation.LuckyClient;
import com.lucky.cloud.client.proxy.LuckyHttpClientProxy;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.container.factory.Namer;
import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午12:30
 */
public class LuckyCloudHttpClientBeanFactory extends IOCBeanFactory {

    private static final Logger log= LoggerFactory.getLogger("c.l.c.c.b.LuckyCloudHttpClientBeanFactory");

    @Override
    public List<Module> createBean() {
        List<Module> beans = super.createBean();
        List<Class<?>> luckyHttpClientClass = getPluginByAnnotation(LuckyClient.class);
        for (Class<?> httpClientClass : luckyHttpClientClass) {
            Module module = new Module(getBeanName(httpClientClass),
                    getBeanType(httpClientClass),
                    LuckyHttpClientProxy.getHttpClientProxy(httpClientClass));
            beans.add(module);
            log.info("Create Lucky Http Client Proxy Bean `{}`",module.getComponent());
        }
        return beans;
    }

    public String getBeanName(Class<?> aClass) {
        String id = AnnotationUtils.get(aClass, LuckyClient.class).id();
        return Assert.isBlankString(id)? Namer.getBeanName(aClass) :id;
    }
}
