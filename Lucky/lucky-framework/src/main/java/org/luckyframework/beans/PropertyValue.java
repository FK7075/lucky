package org.luckyframework.beans;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/21 下午11:58
 */
public class PropertyValue {

    /** 属性名*/
    private String name;
    /** 属性值*/
    private Object value;


    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
