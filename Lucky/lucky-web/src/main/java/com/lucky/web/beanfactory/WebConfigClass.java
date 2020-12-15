package com.lucky.web.beanfactory;

import com.lucky.framework.scan.ConfigClass;
import com.lucky.web.initializer.LuckyDispatcherServletInitializer;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/16 上午2:18
 */
public class WebConfigClass implements ConfigClass {

    @Override
    public Class<?>[] getClasses() {
        return new Class[]{LuckyDispatcherServletInitializer.class,LuckyWebBeanFactory.class};
    }
}
