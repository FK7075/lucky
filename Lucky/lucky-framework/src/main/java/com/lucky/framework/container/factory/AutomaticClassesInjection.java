package com.lucky.framework.container.factory;

import java.util.Set;

/**
 * 自动Class注入器
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/12 上午2:33
 */
public interface AutomaticClassesInjection {

    Set<Class<?>> getConfigComponentClasses();

}
