package com.lucky.web.core.parameter.analysis;

import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.web.annotation.CallApi;
import com.lucky.web.annotation.CallBody;
import com.lucky.web.annotation.Controller;
import com.lucky.web.core.Model;
import com.lucky.web.enums.Rest;
import com.lucky.web.exception.NotFindRequestException;
import com.lucky.web.exception.NotFoundCallUrlException;
import com.lucky.web.httpclient.HttpClientCall;
import com.lucky.web.httpclient.callcontroller.Api;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Api类型的参数解析
 *
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 9:21
 */
public class CallApiParameterAnalysis implements ParameterAnalysis {


    @Override
    public double priority() {
        return 1;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter, String asmParamName) {
        return method.isAnnotationPresent(CallApi.class) && parameter.isAnnotationPresent(CallBody.class);
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter, Type genericParameterType, String asmParamName) throws Exception {
        return httpClientParam(model,method,parameter,genericParameterType);
    }

    /**
     * 得到调用远程接口的返回结果
     * @param method          当前Controller方法
     * @param currParameter   当前参数的Parameter
     * @param model           Model对象
     * @return
     * @throws IOException
     */
    private Object httpClientParam(Model model,Method method,Parameter currParameter,Type genericParameterType) throws Exception {
        String callResult;
        Class<?> controllerClass=method.getDeclaringClass();
        String api = getCallApi(controllerClass, method);
        Map<String, Object> requestMap = getHttpClientRequestParam(model,method);
        callResult = HttpClientCall.call(api, model.getRequestMethod(), requestMap);
        return callRestAndBody(model,currParameter,genericParameterType, callResult);
    }

    /**
     * 处理远程服务返回的数据
     * 1.Rest.TXT  ----> String
     * 2.Rest.JSON ----> JavaObject
     * 3.Rest.XML  ----> JavaObject
     * @param currParameter 当前方法对应的Parameter
     * @param callResult     远程服务响应的String类型结果
     * @return
     */
    private Object callRestAndBody(Model model,Parameter currParameter,Type genericParameterType,  String callResult) throws Exception {
        Rest rest= AnnotationUtils.get(currParameter, CallBody.class).value();
        if(rest==Rest.JSON){
            return model.fromJson(genericParameterType,callResult);
        }
        if(rest==Rest.XML){
            return model.fromXml(genericParameterType,callResult);
        }
        return callResult;
    }

    /**
     * 得到远程服务的Url地址
     * @param controllerClass 当前ControllerClass
     * @param method          当前的ControllerMethod
     * @return callapi
     */
    private String getCallApi(Class<?> controllerClass,Method method) {
        CallApi callApi = method.getAnnotation(CallApi.class);
        String methodCallApi = callApi.value();
        //@CallApi注解中的value()为一个完整的url
        if (methodCallApi.startsWith("${") || methodCallApi.startsWith("http://") || methodCallApi.startsWith("https://")) {
            return Api.getApi(methodCallApi);
        }

        //@CallApi注解中的value()不是一个完整的Url，需要与ControllerApi进行拼接
        String controllerCallApi=method.getAnnotation(Controller.class).callapi();
        if ("".equals(controllerCallApi)) {
            throw new NotFoundCallUrlException("找不到可使用的远程服务地址，错误的远程服务方法：" + method);
        }
        if (!controllerCallApi.endsWith("/")) {
            controllerCallApi += "/";
        }
        if (methodCallApi.startsWith("/")) {
            methodCallApi = methodCallApi.substring(1);
        }
        return controllerCallApi + methodCallApi;
    }


    /**
     * 得到访问远程服务需要的参数
     * @param method     当前Controller方法
     * @param model      Model对象
     * @return
     */
    private Map<String, Object> getHttpClientRequestParam(Model model,Method method) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        String[] apiParamName=method.getAnnotation(CallApi.class).paramNames();
        for (String paramName : apiParamName) {
            paramName=paramName.trim();
            if(!model.parameterMapContainsKey(paramName)){
                if(paramName.contains(":def(")&&paramName.endsWith(")")){
                    String[] kv=paramName.split(":def\\(");
                    map.put(kv[0],kv[1].substring(0,kv[1].length()-1));
                }else{
                    throw new NotFindRequestException("远程API调用时缺少请求参数：" + paramName + ",错误位置：" + method);
                }
            }
            map.put(paramName,model.getParameter(paramName));
        }
        return map;
    }
}
