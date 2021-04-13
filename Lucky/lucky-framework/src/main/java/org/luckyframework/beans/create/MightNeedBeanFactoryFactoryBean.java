package org.luckyframework.beans.create;

import org.luckyframework.beans.factory.BeanFactory;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 9:26
 */
public interface MightNeedBeanFactoryFactoryBean extends FactoryBean {

    boolean needBeanFactory();

    void setBeanFactory(BeanFactory beanFactory);
}
