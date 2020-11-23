package com.lucky.web.httpclient.callcontroller;

import com.lucky.framework.proxy.CglibProxy;

public class CallControllerProxy {

    /**
     * 获得CallController接口的代理对象
     * @param callControllerClass
     * @param <T>
     * @return
     */
    public static <T> T getCallControllerProxyObject(Class<T> callControllerClass){
        return CglibProxy.getCglibProxyObject(callControllerClass,new CallControllerMethodInterceptor(callControllerClass));
    }
}
