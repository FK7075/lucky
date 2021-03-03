package com.lucky.framework.container.lifecycle;

import com.lucky.framework.container.SingletonContainer;
import com.lucky.utils.reflect.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 容器生命周期管理器
 * @author fk
 * @version 1.0
 * @date 2021/3/3 0003 11:45
 */
public class ContainerLifecycleMange {

    private static List<ContainerLifecycle> containerLifecycles;

    public ContainerLifecycleMange(Set<Class<?>> containerLifecycleClasses) {
        containerLifecycles=new ArrayList<>();
        for (Class<?> aClass : containerLifecycleClasses) {
            Class<ContainerLifecycle> LifecycleClass= (Class<ContainerLifecycle>) aClass;
            containerLifecycles.add(ClassUtils.newObject(LifecycleClass));
        }
    }

    public ContainerLifecycleMange(){
        containerLifecycles=new ArrayList<>();
    }

    public void addContainerLifecycle(Class<? extends ContainerLifecycle> lifecycleClass){
        containerLifecycles.add(ClassUtils.newObject(lifecycleClass));
    }

    public void addContainerLifecycle(ContainerLifecycle lifecycle){
        containerLifecycles.add(lifecycle);
    }

    public void beforeContainerInitialized(Set<Class<?>> allBeanClass){
        for (ContainerLifecycle containerLifecycle : containerLifecycles) {
            containerLifecycle.beforeContainerInitialized(allBeanClass);
        }
    }

    public void beforeCreatingInstance(Class<?> beanClass, String beanName, String beanType) {
        for (ContainerLifecycle containerLifecycle : containerLifecycles) {
            containerLifecycle.beforeCreatingInstance(beanClass,beanName,beanType);
        }
    }

    public void instanceCreatedButNoAttributesInjected(SingletonContainer singletonPool) {
        for (ContainerLifecycle containerLifecycle : containerLifecycles) {
            containerLifecycle.instanceCreatedButNoAttributesInjected(singletonPool);
        }
    }

    public void afterContainerInitialized(SingletonContainer singletonPool) {
        for (ContainerLifecycle containerLifecycle : containerLifecycles) {
            containerLifecycle.afterContainerInitialized(singletonPool);
        }
    }
}
