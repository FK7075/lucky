package com.lucky.framework.container.enums;

import com.lucky.framework.FusionStrategy;
import com.lucky.framework.container.SingletonContainer;

import java.util.Set;

/**
 *
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/1 上午12:17
 */
public enum Strategy implements FusionStrategy {

    /**
     * 新单例池和插件池【替换】原有的
     */
    REPLACE(1,1),

    /**
     * 【沿用】旧的单例池和插件池
     */
    CONTINUE(2,2),

    /**
     * 使用新的单例池和插件次【补充】原有的
     */
    SUPPLEMENT(3,3),

    /***
     * 新的【单例池替换】原有的，【插件池沿用】原来的
     */
    REPLACE_STRATEGY(1,2),

    /***
     * 新的【单例池补充】原有的，【插件池沿用】原来的
     */
    SUPPLEMENT_STRATEGY(3,2),

    /**
     * 【单例池沿用】原有的，新的【插件池替换】原有的，
     */
    REPLACE_PIUGIN(2,1),

    /**
     * 【单例池沿用】原有的，新的【插件池补充】原有的，
     */
    SUPPLEMENT_PIUGIN(2,3);



    private int pool_strategy;
    private int plugin_strategy;

    Strategy(int pool_strategy, int plugin_strategy) {
        this.pool_strategy = pool_strategy;
        this.plugin_strategy = plugin_strategy;
    }

    @Override
    public SingletonContainer singletonPoolStrategy(SingletonContainer oldPool, SingletonContainer newPool) {
        //替换
        if(pool_strategy==1){
           return newPool;
        }
        //补充
        else if(pool_strategy==3){
            oldPool.putAll(newPool);
            return oldPool;
        }

        return oldPool;
    }

    @Override
    public Set<Class<?>> pluginsStrategy(Set<Class<?>> oldPlugins, Set<Class<?>> newPlugins) {
        //替换
        if(plugin_strategy==1){
           return newPlugins;
        }
        //补充
        else if(plugin_strategy==3){
            oldPlugins.addAll(newPlugins);
            return oldPlugins;
        }
        return oldPlugins;
    }
}
