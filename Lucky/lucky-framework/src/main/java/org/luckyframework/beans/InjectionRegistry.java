package org.luckyframework.beans;

import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 14:27
 */
public interface InjectionRegistry {

    /**
     * 注册一个Injection
     */
    void registerInjection(Injection injection);

    /**
     * 获取所有注册的Injection
     */
    List<Injection> getInjections();
}
