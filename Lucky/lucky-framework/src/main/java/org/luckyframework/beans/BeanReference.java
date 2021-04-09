package org.luckyframework.beans;

import org.luckyframework.beans.factory.BeanFactory;

/**
 * 用来描述依赖
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/13 上午10:06
 */
public class BeanReference {

    /** 依赖ID*/
    private String beanName;
    /** 注入方式*/
    private Autowire autowire;
    /** 依赖类型*/
    private Class<?> type;
    /** 是否为必须*/
    private boolean required = true;

    public BeanReference(String beanName) {
        this.beanName = beanName;
        autowire=Autowire.BY_NAME;
    }

    public BeanReference(String beanName, Class<?> type) {
        this.beanName = beanName;
        this.type = type;
        autowire=Autowire.BY_TYPE;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Autowire getAutowire() {
        return autowire;
    }

    public void setAutowire(Autowire autowire) {
        this.autowire = autowire;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getReferenceType(BeanFactory beanFactory){
        if(autowire == Autowire.BY_TYPE){
            return type;
        }
        return beanFactory.getType(beanName);
    }
}
