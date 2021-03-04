package com.lucky.framework.container.v2.factory;

import com.lucky.framework.container.v2.factory.impl.DefaultObjectFactory;
import com.lucky.utils.base.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author fk
 * @version 1.0
 * @date 2021/3/4 0004 15:02
 */
public class ObjectFactoryManage {

    private static Map<String,ObjectFactory> factoryMap=new ConcurrentHashMap<>(100);

    static {
        factoryMap.put("component",new DefaultObjectFactory());
    }

    public static ObjectFactory getObjectFactory(String beanType){
        ObjectFactory factory = factoryMap.get(beanType);
        Assert.notNull(factory,"beanType为`"+beanType+"`的ObjectFactory没有注册！");
        return factory;
    }
}
