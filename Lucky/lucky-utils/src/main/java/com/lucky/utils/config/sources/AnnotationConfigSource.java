package com.lucky.utils.config.sources;

import java.util.Set;

/**
 * 注解配置源
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/5 下午6:21
 */
public abstract class AnnotationConfigSource implements ConfigSource{

    private final Set<Class<?>> configClasses;

    public AnnotationConfigSource(Set<Class<?>> configClasses) {
        this.configClasses = configClasses;
    }
}
