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
     * 【单例池替换】【插件池替换】
     */
    REPLACE(1,1),

    /***
     * 【单例池替换】【插件池沿用】
     */
    REPLACE_SINGLETON(1,2),

    /**
     * 【单例池替换】【插件池补充】
     */
    REPLACE_SINGLETON_SUPPLEMENT_PLUGINS(1,3),

    /**
     * 【单例池沿用】【插件池替换】
     */
    REPLACE_PLUGINS(2,1),

    /**
     * 【单例池沿用】【插件池沿用】
     */
    CONTINUE(2,2),

    /**
     * 【单例池沿用】【插件池补充】
     */
    CONTINUE_SINGLETON_SUPPLEMENT_PLUGINS(2,3),

    /**
     * 【单例池补充】【插件池替换】
     */
    SUPPLEMENT_SINGLETON_REPLACE_PLUGINS(3,1),

    /***
     * 【单例池补充】【插件池沿用】
     */
    SUPPLEMENT_SINGLETON(3,2),

    /**
     * 【单例池补充】【插件池补充】
     */
    SUPPLEMENT(3,3);


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
        //沿用
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
        //沿用
        return oldPlugins;
    }
}
