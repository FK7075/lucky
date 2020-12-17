package com.lucky.quartz.proxy;

import com.lucky.utils.proxy.CglibProxy;

public class QuartzProxy {

    private static QuartzMethodInterceptor quartzMethodInterceptor;

    static{
        quartzMethodInterceptor=new QuartzMethodInterceptor();
    }

    public static <T> T getProxy(Class<T> jobClass){
        return CglibProxy.getCglibProxyObject(jobClass,quartzMethodInterceptor);
    }
}
