package org.luckyframework.beans.aware;

import org.luckyframework.beans.factory.BeanFactory;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/4/12 上午12:17
 */
public interface BeanFactoryAware {

    void setBeanFactory(BeanFactory beanFactory);
}
