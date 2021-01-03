package com.lucky.web.interceptor;

import com.lucky.web.core.Model;
import com.lucky.web.mapping.UrlMapping;

/**
 * 拦截器
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/3 上午3:33
 */
public interface HandlerInterceptor {

    /**
     * 拦截器执行的优先级
     * @return
     */
    default double priority(){
        return 5;
    }

    /**
     * 该方法会在控制器方法前执行，其返回值表示是否中断后续操作。当其返回值为true时，表示继续向下执行；
     * 当其返回值为false时，会中断后续的所有操作（包括调用下一个拦截器和控制器类中的方法执行等）
     * @param model
     * @param mapping
     * @return
     * @throws Exception
     */
    default boolean preHandle(Model model, UrlMapping mapping)
            throws Exception {
        return true;
    }

    /**
     * 该方法会在控制器方法调用之后，且解析视图之前执行。可以通过此方法对请求域中的模型和视图做出进一步的修改。
     * @param model
     * @param mapping
     * @throws Exception
     */
    default void postHandle(Model model, UrlMapping mapping) throws Exception {
    }

    /**
     * 该方法会在整个请求完成，即视图渲染结束之后执行。可以通过此方法实现一些资源清理、记录日志信息等工作。
     * @param model
     * @param mapping
     * @param ex
     * @throws Exception
     */
    default void afterCompletion(Model model, UrlMapping mapping, Exception ex) throws Exception {
    }
}
