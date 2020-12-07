package com.lucky.framework.container;

import java.util.Set;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/1 上午12:32
 */
public interface FusionStrategy {

    SingletonContainer singletonPoolStrategy(SingletonContainer oldPool,SingletonContainer newPool);

    Set<Class<?>> pluginsStrategy(Set<Class<?>> oldPlugins,Set<Class<?>> newPlugins);
}
