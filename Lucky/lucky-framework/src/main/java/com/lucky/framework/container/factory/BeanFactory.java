package com.lucky.framework.container.factory;

import com.lucky.framework.container.Module;

import java.util.List;
import java.util.Map;

/**
 * 创建Bean的工厂
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 7:05 上午
 */
public interface BeanFactory {

    /**
     * 根据自定义的规则创建一系列的Bean，这些Bean将会被注册到IOC容器中
     * @return 创建出的实例集合
     */
    List<Module> createBean();

    /**
     * 使用的特定的Bean来替换掉一些特殊的Bean
     * @return 要覆盖的Bean的ID与覆盖后的Bean所组成的Map
     */
    Map<String,Module> replaceBean();

    /**
     * 优先级
     * @return
     */
    double priority();

}
