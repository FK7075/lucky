package com.lucky.web.httpclient.callcontroller;

import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.web.annotation.CallController;
import com.lucky.web.annotation.RequestMapping;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;

import static com.lucky.web.httpclient.HttpProxyUtils.*;

public class CallControllerMethodInterceptor implements MethodInterceptor {

    private Class<?> callControllerClass;

    public CallControllerMethodInterceptor(Class<?> callControllerClass) {
        this.callControllerClass = callControllerClass;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        if(AnnotationUtils.strengthenIsExist(method, RequestMapping.class)){
            //获取远程接口的地址
            MappingDetails md;
            String apiUrl;
            String callControllerApi=Api.getApi(callControllerClass.getAnnotation(CallController.class).value());
            md=getMappingDetails(method);
            String methodApi=Api.getApi(md.value);
            if(methodApi.startsWith("${")||methodApi.startsWith("http://")||methodApi.startsWith("https://")){
                apiUrl=Api.getApi(methodApi);
            }else{
                callControllerApi=callControllerApi.endsWith("/")?callControllerApi:callControllerApi+"/";
                methodApi=methodApi.startsWith("/")?methodApi.substring(1):methodApi;
                apiUrl=callControllerApi+methodApi;
            }
            //处理Rest风格URL中的{xxx}参数
            UrlAndParamMap urlAndParamMap = new UrlAndParamMap(apiUrl, getParamMap(method,params));
            apiUrl=urlAndParamMap.getUrl();
            Map<String,Object> callApiMap=urlAndParamMap.getParamMap();
            int callType = getCallType(method, callApiMap);
            //调用远程接口
            Object callResult=call(apiUrl,md.method[0],callApiMap,callType);
            //封装返回结果
            return resultProcess(method,callResult,apiUrl);
        }
        return  methodProxy.invokeSuper(o,params);
    }
}
