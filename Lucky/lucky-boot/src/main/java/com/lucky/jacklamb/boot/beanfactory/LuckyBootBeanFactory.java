package com.lucky.jacklamb.boot.beanfactory;

import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.jacklamb.boot.annotation.LEndpoint;
import com.lucky.jacklamb.boot.annotation.LuckyFilter;
import com.lucky.jacklamb.boot.annotation.LuckyListener;
import com.lucky.jacklamb.boot.annotation.LuckyServlet;

import javax.websocket.server.ServerEndpoint;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * BOOT
 * @author fk
 * @version 1.0
 * @date 2020/12/3 0003 15:14
 */
public class LuckyBootBeanFactory extends IOCBeanFactory {

    Class<? extends Annotation>[] WEB_CLASSES=
            new Class[]{LuckyServlet.class, LuckyFilter.class,
                    LuckyListener.class, ServerEndpoint.class, LEndpoint.class};


    @Override
    public Map<String, Module> replaceBean() {
        return super.replaceBean();
    }

    @Override
    public List<Module> createBean() {
        List<Class<?>> pluginByClass = getPluginByClass(WEB_CLASSES);
        return super.createBean();
    }

}
