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
     * 该方法会在控制器方法前执行，其返回值表示是否中断后续操作。当其返回值为true时，表示继续向下执行；
     * 当其返回值为false时，会中断后续的所有操作（包括调用下一个拦截器和控制器类中的方法执行等）
     * @param model
     * @param handler
     * @return
     * @throws Exception
     */
    default boolean preHandle(Model model,  final UrlMapping handler)
            throws Exception {
        return true;
    }

    /**
     * 该方法会在控制器方法调用之后，且解析视图之前执行。可以通过此方法对请求域中的模型和视图做出进一步的修改。
     * @param model
     * @param handler
     * @param result
     * @throws Exception
     */
    default Object postHandle(Model model,  final UrlMapping handler,Object result) throws Exception {
        return result;
    }

    /**
     * 该方法会在整个请求完成，即视图渲染结束之后执行。可以通过此方法实现一些资源清理、记录日志信息等工作。
     * @param model
     * @param handler
     * @param ex
     * @throws Exception
     */
    default void afterCompletion(Model model, final UrlMapping handler, Exception ex) throws Exception {
    }
}
