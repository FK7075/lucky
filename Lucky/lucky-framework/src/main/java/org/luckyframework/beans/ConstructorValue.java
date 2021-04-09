package org.luckyframework.beans;

import com.lucky.utils.base.Assert;
import org.luckyframework.beans.factory.BeanFactory;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/25 0025 11:45
 */
public class ConstructorValue {

    private Class<?> type;
    private Object value;

    public Class<?> getType(BeanFactory beanFactory) {
        if(type == null){
            if(value instanceof BeanReference){
                type = ((BeanReference)value).getReferenceType(beanFactory);
            }else{
                type = value.getClass();
            }
        }
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ConstructorValue(Class<?> type, Object value) {
        this.type = type;
        this.value = value;
    }

    public ConstructorValue(Object value){
        Assert.notNull(value,"When assigning a null value to the constructor parameter, the type of the parameter must be provided");
        this.value = value;
    }

    @Override
    public String toString(){
        if(value == null){
            return null;
        }
        if(value instanceof BeanReference){
            return "[REF]"+((BeanReference) value).getBeanName();
        }
        return value.toString();
    }

}
