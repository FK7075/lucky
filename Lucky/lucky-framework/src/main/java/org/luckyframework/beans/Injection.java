package org.luckyframework.beans;

import org.luckyframework.beans.factory.BeanFactory;
import org.luckyframework.beans.factory.ListableBeanFactory;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 14:17
 */
public interface Injection {

    void injection(Object instance, ListableBeanFactory beanFactory);
}
