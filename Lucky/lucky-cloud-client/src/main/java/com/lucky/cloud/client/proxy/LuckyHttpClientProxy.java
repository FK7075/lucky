package com.lucky.cloud.client.proxy;

import com.lucky.utils.proxy.CglibProxy;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午12:24
 */
public class LuckyHttpClientProxy {

    public static <T> T getHttpClientProxy(Class<T> luckyHttpClientClass){
        return CglibProxy.getCglibProxyObject(luckyHttpClientClass,new LuckyCloudClientMethodInterceptor(luckyHttpClientClass));
    }
}
