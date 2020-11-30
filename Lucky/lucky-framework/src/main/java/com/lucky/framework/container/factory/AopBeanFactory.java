package com.lucky.framework.container.factory;

import com.lucky.framework.FusionStrategy;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/28 上午5:01
 */
public class AopBeanFactory extends IOCBeanFactory{

    public AopBeanFactory(){
        super();
    }

    public AopBeanFactory(FusionStrategy fusionStrategy){
        super(fusionStrategy);
    }

    @Override
    public double priority() {
        return 2;
    }
}
