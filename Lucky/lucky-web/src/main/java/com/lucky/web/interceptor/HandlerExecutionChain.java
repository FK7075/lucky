package com.lucky.web.interceptor;


import com.lucky.web.core.Model;
import com.lucky.web.mapping.UrlMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 拦截器链条
 * @author fk
 * @version 1.0
 * @date 2021/1/5 0005 9:02
 */
public class HandlerExecutionChain {

    private static final Logger log = LoggerFactory.getLogger(HandlerExecutionChain.class);

    private final UrlMapping handler;
    private final List<HandlerInterceptor> interceptorList = new ArrayList<>();
    private int interceptorIndex = -1;

    public HandlerExecutionChain(Object handler, HandlerInterceptor... interceptors) {
        this(handler, (interceptors != null ? Arrays.asList(interceptors) : Collections.emptyList()));
    }

    public HandlerExecutionChain(Object handler, List<HandlerInterceptor> interceptorList) {
        if (handler instanceof HandlerExecutionChain) {
            HandlerExecutionChain originalChain = (HandlerExecutionChain) handler;
            this.handler = originalChain.getHandler();
            this.interceptorList.addAll(originalChain.interceptorList);
        } else {
            this.handler = (UrlMapping) handler;
        }
        this.interceptorList.addAll(interceptorList);
    }

    /**
     * Return the handler object to execute.
     */
    public UrlMapping getHandler() {
        return this.handler;
    }

   public boolean applyPreHandle(Model model) throws Exception {
        for (int i = 0; i < this.interceptorList.size(); i++) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            if (!interceptor.preHandle(model, this.handler)) {
                triggerAfterCompletion(model, null);
                return false;
            }
            this.interceptorIndex = i;
        }
        return true;
    }

    public Object applyPostHandle(Model model,Object result)
            throws Exception {

        for (int i = this.interceptorList.size() - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            result=interceptor.postHandle(model, this.handler, result);
        }
        return result;
    }

    public 	void triggerAfterCompletion(Model model, Throwable ex) {
        for (int i = this.interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            try {
                interceptor.afterCompletion(model, this.handler, ex);
            }
            catch (Throwable ex2) {
                log.error("HandlerInterceptor.afterCompletion threw exception", ex2);
            }
        }
    }

    /**
     * Delegates to the handler's {@code toString()} implementation.
     */
    @Override
    public String toString() {
        return "HandlerExecutionChain with [" + getHandler() + "] and " + this.interceptorList.size() + " interceptors";
    }
}
