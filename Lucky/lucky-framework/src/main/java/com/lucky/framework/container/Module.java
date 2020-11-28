package com.lucky.framework.container;

/**
 * IOC模型
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 1:14 下午
 */
public class Module {

    /**Bean的唯一标识*/
    private String id;
    /**组件类型*/
    private String type="component";
    /** 组件的原始类型*/
    private Class<?> originalType;
    /**组件实例*/
    private Object component;

    public Module(String id, Object component) {
        this.id = id;
        this.component = component;
        this.originalType=component.getClass();
    }

    public Module(String id, String type, Object component) {
        this.id = id;
        this.component = component;
        this.type = type;
        this.originalType=component.getClass();
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
        return "Module{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", originalType=" + originalType +
                ", component=" + component +
                '}';
    }
}
