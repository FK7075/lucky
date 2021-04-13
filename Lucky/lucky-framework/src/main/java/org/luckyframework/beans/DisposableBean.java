package org.luckyframework.beans;

/**
 *
 * @author fk
 * @version 1.0
 * @date 2021/3/26 0026 11:17
 */
public interface DisposableBean {

    void destroy() throws Exception;
}
