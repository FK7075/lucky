package org.luckyframework.beans.create;

import com.lucky.utils.type.ResolvableType;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/9 0009 11:47
 */
public interface FactoryBean {

     Object getBean();

     ResolvableType getBeanType();

}
