package com.lucky.framework.container.v2;

import com.lucky.framework.container.Module;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/4 0004 15:18
 */
public class SingletonContainer {

    /** 单例池*/
    private Map<String, Object> singletonObjects =new ConcurrentHashMap<>(256);

    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    public Object getSingleton(String beanName){
        Object singletonObject = this.singletonObjects.get(beanName);
        if(singletonObject==null){
             singletonObject=this.earlySingletonObjects.get(beanName);
        }
        return singletonObject;
    }
}
