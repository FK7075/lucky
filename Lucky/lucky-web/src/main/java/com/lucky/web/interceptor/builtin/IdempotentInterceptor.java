package com.lucky.web.interceptor.builtin;

import com.lucky.web.core.Model;
import com.lucky.web.interceptor.HandlerInterceptor;
import com.lucky.web.mapping.UrlMapping;

/**
 * 可以保证接口幂等性的拦截器
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/17 下午3:01
 */
public class IdempotentInterceptor implements HandlerInterceptor {

    private static final String END_OF_RESPONSE="?_END_OF_REQUEST_?";
    /*

     */

    @Override
    public boolean preHandle(Model model, UrlMapping handler) throws Exception {
        String handlerId = handler.getId();
        Object token = model.getSessionAttribute(handlerId);
        if(token==null){
            model.setSessionAttribute(handlerId,"");
            return true;
        }
        return false;
    }

    @Override
    public Object postHandle(Model model, UrlMapping handler, Object result) throws Exception {
        return result;
    }

    @Override
    public void afterCompletion(Model model, UrlMapping handler, Throwable ex) throws Exception {

    }
}
