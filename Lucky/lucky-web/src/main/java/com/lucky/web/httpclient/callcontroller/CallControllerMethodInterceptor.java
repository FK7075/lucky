package com.lucky.web.httpclient.callcontroller;

import com.lucky.framework.proxy.ASMUtil;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.framework.uitls.reflect.MethodUtils;
import com.lucky.framework.uitls.reflect.ParameterUtils;
import com.lucky.web.annotation.CallController;
import com.lucky.web.annotation.FileDownload;
import com.lucky.web.annotation.FileUpload;
import com.lucky.web.annotation.RequestMapping;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.exception.JsonConversionException;
import com.lucky.web.exception.NotMappingMethodException;
import com.lucky.web.httpclient.HttpClientCall;
import com.lucky.web.mapping.MappingAnalysis;
import com.lucky.web.webfile.MultipartFile;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallControllerMethodInterceptor implements MethodInterceptor {

    private Class<?> callControllerClass;
    private static final WebConfig webConfig=WebConfig.getWebConfig();

    public CallControllerMethodInterceptor(Class<?> callControllerClass) {
        this.callControllerClass = callControllerClass;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        if(MethodUtils.isObjectMethod(method)){
            return methodProxy.invokeSuper(o,params);
        }
        if(!AnnotationUtils.isExistOrByArray(method, MappingAnalysis.MAPPING_ANNOTATIONS)){
            throw new NotMappingMethodException("该方法不是Mapping方法，无法执行代理！错误位置："+method);
        }

        Map<String,Object> callapiMap=getParamMap(method,params);
        //获取远程接口的地址
        MappingDetails md;
        String apiUrl;
        String callControllerApi=Api.getApi(callControllerClass.getAnnotation(CallController.class).value());
        md=getMappingDetails(method);
        String methodApi=Api.getApi(md.value);
        if(methodApi.startsWith("${")||methodApi.startsWith("http://")||methodApi.startsWith("https://")){
            apiUrl=Api.getApi(methodApi);
        }else{
            if(!callControllerApi.endsWith("/"))
                callControllerApi+="/";
            if(methodApi.startsWith("/"))
                methodApi=methodApi.substring(1);
            apiUrl=callControllerApi+methodApi;
        }

        //文件下载的请求，服务将返回byte[]类型的结果
        if(method.isAnnotationPresent(FileDownload.class)){
            return HttpClientCall.callByte(apiUrl,md.method[0],callapiMap);
        }

        //调用远程接口
        String callResult=call(apiUrl,method,callapiMap,md.method[0]);

        //封装返回结果
        Class<?> returnClass=method.getReturnType();
        if(returnClass!=void.class){
            if(returnClass==String.class){
                return callResult;
            }else{
                try{
                    return webConfig.getJsonSerializationScheme().deserialization(returnClass,callResult);
                }catch (Exception e){
                    throw new JsonConversionException(apiUrl,returnClass,callResult,e);
                }
            }
        }
        return null;
    }


    /**
     * 发起请求
     * @param url 服务的URL
     * @param method 当前方法
     * @param params 方法执行的参数列表
     * @param requestMethod 请求类型
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static String call(String url, Method method, Map<String,Object> params, RequestMethod requestMethod) throws IOException, URISyntaxException {
        if(!method.isAnnotationPresent(FileUpload.class))
            return HttpClientCall.call(url,requestMethod,params);
        return HttpClientCall.uploadFile(url,params);
    }

    /**
     * 获取并封装请求远程接口的参数
     * @param method
     * @param params
     * @return
     * @throws IOException
     * @throws IllegalAccessException
     */
    private static Map<String,Object> getParamMap(Method method,Object[] params) throws IOException, IllegalAccessException {
        Map<String,Object> callapiMap=new HashMap<>();
        Parameter[] parameters=method.getParameters();
        List<String> paramName= ASMUtil.getInterfaceMethodParamNames(method);
        String key=null;
        for(int i=0;i<parameters.length;i++) {
            Class<?> paramClass=params[i].getClass();
            //可以直接put的类型：JDK自带的类型、MultipartFile、MultipartFile[]
            if(paramClass.getClassLoader()==null||paramClass== MultipartFile.class||paramClass== MultipartFile[].class){
                key = ParameterUtils.getParamName(parameters[i],paramName.get(i));
                callapiMap.put(key, params[i]);
            }else{
                Field[] fields = ClassUtils.getAllFields(paramClass);
                Object fieldValue;
                for(Field field:fields){
                    field.setAccessible(true);
                    fieldValue=field.get(params[i]);
                    if(fieldValue!=null)
                        callapiMap.put(field.getName(),fieldValue.toString());
                }
            }
        }
        return callapiMap;
    }

    private MappingDetails getMappingDetails(Method method){
        MappingDetails md=new MappingDetails();
        Annotation mappingAnnotation = AnnotationUtils.getByArray(method, MappingAnalysis.MAPPING_ANNOTATIONS);
        md.ip= (String[]) AnnotationUtils.getValue(mappingAnnotation,"ip");
        md.value= (String) AnnotationUtils.getValue(mappingAnnotation,"value");
        md.ipSection= (String[]) AnnotationUtils.getValue(mappingAnnotation,"ipSection");
        md.method=AnnotationUtils.strengthenGet(method, RequestMapping.class).get(0).method();
        return md;
    }
}
