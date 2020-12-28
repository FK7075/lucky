package com.lucky.web.httpclient.callcontroller;

import com.lucky.utils.proxy.ASMUtil;
import com.lucky.utils.reflect.*;
import com.lucky.web.annotation.*;
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
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            callControllerApi=callControllerApi.endsWith("/")?callControllerApi:callControllerApi+"/";
            methodApi=methodApi.startsWith("/")?methodApi.substring(1):methodApi;
            apiUrl=callControllerApi+methodApi;
        }

        //处理Rest风格的参数
        if(apiUrl.contains("{")&&apiUrl.contains("}")){
            UrlAndParamMap urlAndParamMap = new UrlAndParamMap(apiUrl, callapiMap);
            apiUrl=urlAndParamMap.getUrl();
            callapiMap=urlAndParamMap.getParamMap();
        }

        //文件下载的请求，服务将返回byte[]类型的结果
        if(method.isAnnotationPresent(FileDownload.class)){
            return HttpClientCall.callByte(apiUrl,md.method[0],callapiMap);
        }

        //调用远程接口
        String callResult=call(apiUrl,method,callapiMap,md.method[0]);

        //封装返回结果
        Type returnClass=method.getGenericReturnType();
        if(returnClass!=void.class){
            if(returnClass==String.class){
                return callResult;
            }else{
                try{
                    return webConfig.getJsonSerializationScheme().deserialization(returnClass,callResult);
                }catch (Exception e){
                    callResult=callResult.length()>=225?callResult.substring(0,225):callResult;
                    throw new JsonConversionException(apiUrl,method.getReturnType(),callResult);
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
        if(!method.isAnnotationPresent(FileUpload.class)) {
            return HttpClientCall.call(url,requestMethod,params);
        }
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
            if(AnnotationUtils.isExist(parameters[i], RequestBody.class)){
                String serialization = webConfig.getJsonSerializationScheme().serialization(params[i]);
                callapiMap.put(UUID.randomUUID().toString(),new JSONObject(serialization));
                continue;
            }
            Class<?> paramClass=params[i].getClass();
            //可以直接put的类型：JDK自带的类型、MultipartFile、MultipartFile[]
            if(ClassUtils.isBasic(paramClass)||paramClass== MultipartFile.class||paramClass== MultipartFile[].class){
                key = ParameterUtils.getParamName(parameters[i],paramName.get(i));
                callapiMap.put(key, params[i]);
            }else{
                Field[] fields = ClassUtils.getAllFields(paramClass);
                Object fieldValue;
                for(Field field:fields){
                    fieldValue= FieldUtils.getValue(params[i],field);
                    if(fieldValue!=null) {
                        callapiMap.put(field.getName(),fieldValue.toString());
                    }
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

    //处理Rest参数的内部类
    class UrlAndParamMap{
        private String url;
        private Map<String,Object> paramMap;

        public String getUrl() {
            return url;
        }

        public Map<String, Object> getParamMap() {
            return paramMap;
        }

        public UrlAndParamMap(String url,Map<String,Object> paramMap){
            String[] urlElements = url.split("/");
            StringBuilder newUrl=new StringBuilder();
            for (int i=0,j=urlElements.length;i<j; i++) {
                String element=urlElements[i];
                if((element.startsWith("#{")||element.startsWith("{"))&&element.endsWith("}")){
                    String urlParamName;
                    if(element.startsWith("#{")){
                        urlParamName=element.substring(2,element.length()-1);
                    }else{
                        urlParamName=element.substring(1,element.length()-1);
                    }
                    if(paramMap.containsKey(urlParamName)){
                        urlElements[i]=paramMap.get(urlParamName).toString();
                        paramMap.remove(urlParamName);
                    }else{
                        urlElements[i]="";
                    }
                }
                newUrl.append(urlElements[i]).append("/");
            }
            this.paramMap=paramMap;
            this.url=newUrl.toString();
        }

    }
}
