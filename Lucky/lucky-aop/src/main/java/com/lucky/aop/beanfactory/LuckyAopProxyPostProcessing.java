package com.lucky.aop.beanfactory;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.container.FusionStrategy;
import com.lucky.framework.container.SingletonContainer;
import com.lucky.framework.container.lifecycle.ContainerLifecycle;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/10 0010 15:31
 */
@Component
public class LuckyAopProxyPostProcessing extends BaseAopBeanFactory implements ContainerLifecycle {

    public LuckyAopProxyPostProcessing(){
        super();
    }

    public LuckyAopProxyPostProcessing(FusionStrategy fusionStrategy){
        super(fusionStrategy);
    }

    @Override
    public void afterContainerInitialized(SingletonContainer singletonPool) {
        loadPoint();
        proxy(singletonPool.values(),true);
    }

}
