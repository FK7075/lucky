package com.lucky.cloud.client.proxy;

import com.lucky.cloud.client.annotation.LuckyClient;
import com.lucky.cloud.client.annotation.RegistryRequest;
import com.lucky.cloud.client.annotation.ServerRequest;
import com.lucky.cloud.client.core.ServiceCall;
import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.web.annotation.RequestMapping;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.mapping.MappingAnalysis;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import static com.lucky.web.httpclient.HttpProxyUtils.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/31 上午12:22
 */
public class LuckyCloudClientMethodInterceptor implements MethodInterceptor {

    private final Class<?> luckyHttpClientClass;

    public LuckyCloudClientMethodInterceptor(Class<?> luckyHttpClientClass) {
        this.luckyHttpClientClass = luckyHttpClientClass;
    }

    @Override
    public Object intercept(Object target, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        if(AnnotationUtils.strengthenIsExist(method, RequestMapping.class)){
            LuckyClient luckyClient = luckyHttpClientClass.getAnnotation(LuckyClient.class);
            Class<?> targetClass=CglibProxy.getOriginalType(target.getClass());
            String registry = luckyClient.registry();
            String serverName = luckyClient.value();
            //处理Rest风格URL中的{xxx}参数
            UrlAndParamMap urlAndParamMap
                    = new UrlAndParamMap(getResource(targetClass, method), getParamMap(method, params));
            String resource = urlAndParamMap.getUrl();
            Map<String, Object> paramMap = urlAndParamMap.getParamMap();
            RequestMethod requestMethod = getRequestMethod(method);
            int callType = getCallType(method,paramMap);
            Object result=isRegistryRequest(targetClass, method)
                    ?ServiceCall.callByRegistry(registry,serverName,resource,paramMap,requestMethod,callType)
                    :ServiceCall.callByServer(registry,serverName,resource,paramMap,requestMethod,callType);
            return resultProcess(method,result,resource);
        }
        return methodProxy.invokeSuper(target,params);
    }


    /**
     * 请求方式是否为 [注册中心转发]
     * @param targetClass 代理类的Class
     * @param method 代理方法
     * @return Y->true; N->false
     */
    private boolean isRegistryRequest(Class<?> targetClass,Method method){
        //如果是文件拉取类型的请求，必须要直接请求对应的服务
        if(isFileRequest(method)){
            return false;
        }
        if(method.isAnnotationPresent(ServerRequest.class)){
            return false;
        }
        if(method.isAnnotationPresent(RegistryRequest.class)){
            return true;
        }
        return !targetClass.isAnnotationPresent(ServerRequest.class);
    }

    private String getResource(Class<?> targetClass,Method method){
        Annotation annotation = AnnotationUtils.getByArray(method, MappingAnalysis.MAPPING_ANNOTATIONS);
        String methodUrl= (String) AnnotationUtils.getValue(annotation,"value");
        //m-url -> book/getBook
        methodUrl=methodUrl.endsWith("/")?methodUrl.substring(0,methodUrl.length()-1):methodUrl;
        methodUrl=methodUrl.startsWith("/")?methodUrl.substring(1):methodUrl;
        RequestMapping classMapping = targetClass.getAnnotation(RequestMapping.class);
        if(classMapping!=null){
            String classUrl = classMapping.value();
            classUrl=classUrl.endsWith("/")?classUrl:classUrl+"/";
            methodUrl=classUrl+methodUrl;
        }
        return methodUrl;
    }
}
