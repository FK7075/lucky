package com.lucky.framework.container.factory;

import com.lucky.utils.base.BaseUtils;

/**
 * 起名器，用于给需要注册的Bean起名字
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/15 下午11:44
 */
public interface Namer {

    /**
     * 返回Bean实例在IOC容器中的唯一ID
     * @param beanClass BeanClass
     * @return 唯一ID
     */
    static String getBeanName(Class<?> beanClass){
        String simpleName = beanClass.getSimpleName();
        if("".equals(simpleName)){
            String classStr = beanClass.toString();
            simpleName=classStr.substring(classStr.lastIndexOf(".")+1);
        }
        return BaseUtils.lowercaseFirstLetter(simpleName);
    }

    /**
     * 返回Bean实例在IOC容器中的类型
     * @param beanClass BeanClass
     * @return Bean实例的类型
     */
    String getBeanType(Class<?> beanClass);
}
