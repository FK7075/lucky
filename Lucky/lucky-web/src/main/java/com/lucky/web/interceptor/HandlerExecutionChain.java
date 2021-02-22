package com.lucky.web.interceptor;


import com.lucky.web.core.Model;
import com.lucky.web.mapping.UrlMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    /** 响应当前请求的UrlMapping*/
    private final UrlMapping handler;
    /** 作用于当前请求的拦截器集合*/
    private final List<HandlerInterceptor> interceptorList = new ArrayList<>();
    /** 索引*/
    private int interceptorIndex = -1;

    public HandlerExecutionChain(Object handler, HandlerInterceptor... interceptors) {
        this(handler, (interceptors != null ? Arrays.asList(interceptors) : Collections.emptyList()));
    }

    public boolean isEmpty(){
        return interceptorList.isEmpty();
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

    /**
     * ##                                      ##
     * --找到UrlMapping之后，执行UrlMapping之前执行--
     * ##                                      ##
     * 执行所有拦截器的preHandle方法，如果此方法返回true，则正常响应请求
     * 返回false则表示请求将会被拦截，不会正常响应
     * @param model Model对象
     * @return
     * @throws Exception
     */
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

    /**
     * ##                                ##
     * --执行UrlMapping之后，响应数据之前执行--
     * ##                                ##
     * 执行所有拦截器的postHandle方法，
     * @param model Model对象
     * @param result UrlMapping执行结果，正常情况下这个结果会直接响应给客户端
     *               当然，最终的响应结果还会受各个拦截器的影响
     * @return 每一个拦截器的postHandle都将会返回一个新的Result，返回的Result可能
     * 是最初的Result也有可能是一个全新的Result，这个新的Result将会被响应给客户端
     * @throws Exception
     */
    public Object applyPostHandle(Model model,Object result)
            throws Exception {

        for (int i = this.interceptorList.size() - 1; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            result=interceptor.postHandle(model, this.handler, result);
        }
        return result;
    }

    /**
     * ##                                      ##
     * --响应数据之后执行，用于释放资源、异常处理等操作--
     * ##                                      ##
     * @param model Model对象
     * @param ex 异常(如果有)，执行过程中未出现异常此参数值将为null
     */
    public 	void triggerAfterCompletion(Model model, Throwable ex) {
        for (int i = this.interceptorIndex; i >= 0; i--) {
            HandlerInterceptor interceptor = this.interceptorList.get(i);
            try {
                if(model.isExceptionIsHandling()){
                    ex=null;
                }
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
