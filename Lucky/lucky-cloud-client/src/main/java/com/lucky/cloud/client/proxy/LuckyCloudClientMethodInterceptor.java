package com.lucky.cloud.client.proxy;

import com.lucky.cloud.client.annotation.LuckyClient;
import com.lucky.cloud.client.annotation.RegistryRequest;
import com.lucky.cloud.client.annotation.ServerRequest;
import com.lucky.cloud.client.core.ServiceCall;
import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.reflect.*;
import com.lucky.web.annotation.RequestMapping;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.httpclient.callcontroller.CallControllerMethodInterceptor;
import com.lucky.web.mapping.MappingAnalysis;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

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
            String resource = getResource(targetClass, method);
            RequestMethod requestMethod = getRequestMethod(method);
            Map<String, Object> paramMap = getParamMap(method, params);
            //处理Rest风格的参数
            if(resource.contains("{")&&resource.contains("}")){
                CallControllerMethodInterceptor.UrlAndParamMap urlAndParamMap
                        = new CallControllerMethodInterceptor.UrlAndParamMap(resource, paramMap);
                resource=urlAndParamMap.getUrl();
                paramMap=urlAndParamMap.getParamMap();
            }
            boolean isByte = isReturnByte(method);
            Object result;
            if(isRegistryRequest(targetClass, method)){
                result=ServiceCall.callByRegistry(registry,serverName,resource,paramMap,requestMethod,isByte);
            }else{
                result=ServiceCall.callByServer(registry,serverName,resource,paramMap,requestMethod,isByte);
            }
            return isByte?result:CallControllerMethodInterceptor.resultProcess(method,(String)result,resource);
        }
        return methodProxy.invokeSuper(target,params);
    }


    private RequestMethod getRequestMethod(Method method){
        return AnnotationUtils.strengthenGet(method, RequestMapping.class).get(0).method()[0];
    }

    private boolean isRegistryRequest(Class<?> targetClass,Method method){
        if(method.isAnnotationPresent(ServerRequest.class)){
            return false;
        }
        if(method.isAnnotationPresent(RegistryRequest.class)){
            return true;
        }
        return !targetClass.isAnnotationPresent(ServerRequest.class);
    }

    private boolean isReturnByte(Method method){
        return method.getReturnType()==byte[].class;
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

    private Map<String,Object> getParamMap(Method method, Object[] params) throws IllegalAccessException, IOException {
        return CallControllerMethodInterceptor.getParamMap(method, params);
    }
}
