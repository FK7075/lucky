package com.lucky.framework.container;

import com.lucky.framework.container.lifecycle.BeanLifecycle;
import com.lucky.utils.type.ResolvableType;

/**
 * IOC模型
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 1:14 下午
 */
public class Module {

    /**组件类型*/
    private String type="component";
    /**Bean的唯一标识*/
    private String id;
    /** 组件的原始类型*/
    private Class<?> originalType;
    /** 组件实例*/
    private Object component;
    /** ResolvableType*/
    private ResolvableType resolvableType;
    /** 是否已经注入属性*/
    private boolean injection;

    public Module(String id, Object component) {
        this(id,"component",component);
    }

    public Module(String id, String type,Object component, ResolvableType resolvableType,boolean isCompose) {
        this.type = type;
        this.id = id;
        this.component = component;
        this.originalType = component.getClass();
        if(isCompose){
            this.resolvableType = ResolvableType.forClass(resolvableType.getRawClass(),originalType);
        }else {
            this.resolvableType = resolvableType;
        }
    }

    public Module(String id, String type,Object component, ResolvableType resolvableType){
        this(id, type, component,resolvableType,false);
    }

    public Module(String id, String type, Object component) {
        this(id,type,component,ResolvableType.forRawClass(component.getClass()));
    }

    public void setOriginalType(Class<?> originalType) {
        this.originalType = originalType;
    }

    public ResolvableType getResolvableType() {
        return resolvableType;
    }

    public void setResolvableType(ResolvableType resolvableType) {
        this.resolvableType = resolvableType;
    }

    public boolean isInjection() {
        return injection;
    }

    public void setInjection(boolean injection) {
        this.injection = injection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getComponent() {
        return component;
    }

    public void setComponent(Object component) {
        this.component = component;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Class<?> getOriginalType() {
        return originalType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Module{");
        sb.append("type='").append(type).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", originalType=").append(originalType);
        sb.append(", component=").append(component);
        sb.append('}');
        return sb.toString();
    }
}
