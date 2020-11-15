package com.lucky.jacklamb.framework.container.factory;

import com.lucky.jacklamb.framework.container.Module;

import java.util.List;
import java.util.Set;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午12:50
 */
public class ConfigurationBeanFactory extends IOCBeanFactory{

    private Set<Class<?>> configurationClassSet;

    public ConfigurationBeanFactory(Set<Class<?>> configurationClassSet) {
        this.configurationClassSet = configurationClassSet;
    }

    @Override
    public List<Module> createBean() {
        return null;
    }

}
