package org.luckyframework.beans;

import com.lucky.utils.base.Assert;
import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.factory.BeanFactory;
import org.luckyframework.exception.BeanCreationException;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/4/11 下午6:54
 */
public class BeanReferenceUtils {

    public Object getRealValue(BeanReference ref, BeanFactory beanFactory){
        String beanName = ref.getBeanName();
        if(beanFactory.containsBean(beanName)){
            throw new BeanCreationException(beanName,"The bean definition information named '"+beanName+"' does not exist in the container");
        }
        return beanFactory.getBean(beanName);
    }

    public ResolvableType getRealType(BeanReference ref){
        return null;
    }

    public Class<?> getRealClass(BeanReference ref){
        ResolvableType realType = getRealType(ref);
        Assert.notNull(realType,"");
        return realType.getRawClass();
    }

    public ResolvableType[] getRealGenericType(BeanReference ref){
        ResolvableType realType = getRealType(ref);
        Assert.notNull(realType,"");
        return realType.getGenerics();
    }
}
