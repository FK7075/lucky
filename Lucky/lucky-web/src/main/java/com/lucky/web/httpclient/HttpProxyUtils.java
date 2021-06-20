package com.lucky.web.httpclient;

import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;
import com.lucky.utils.proxy.ASMUtil;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import com.lucky.utils.reflect.ParameterUtils;
import com.lucky.utils.regula.Regular;
import com.lucky.web.annotation.RequestBody;
import com.lucky.web.annotation.RequestMapping;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.core.BodyObject;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.enums.Rest;
import com.lucky.web.exception.JsonConversionException;
import com.lucky.web.exception.NotMappingMethodException;
import com.lucky.web.httpclient.callcontroller.MappingDetails;
import com.lucky.web.mapping.MappingAnalysis;
import com.lucky.web.webfile.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

/**
 * @author fk
 * @version 1.0
 * @date 2021/2/1 0001 10:18
 */
public abstract class HttpProxyUtils {

    private static final WebConfig webConfig=WebConfig.getWebConfig();

    /** 字符串类型请求*/
    public static final int STRING=0;
    /** 文件上传类型请求*/
    public static final int MULTIPART_FILE=1;
    /** 文件拉取类型请求 -> byte[]*/
    public static final int BYTE_ARRAY=2;
    /** 文件拉取类型请求 -> InputStream*/
    public static final int BYTE_INPUT_STREAM=3;
    /** JSON对象类型请求*/
    public static final int JSON=3;

    /**
     * 访问某个网络资源
     * @param apiURL   资源的完整URL
     * @param method   请求使用的方法[GET/POST/DELETE/PUT/...]
     * @param param    参数列表
     * @param callType 请求类型[文件请求/字符串请求/byte[]请求/InputStream请求]
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static Object call(String apiURL, RequestMethod method, Map<String,Object> param, int callType) throws IOException, URISyntaxException {
        if(STRING==callType){
            return HttpUtils.executeReturnString(apiURL, method, param);
        }
        if(MULTIPART_FILE==callType){
            return HttpUtils.executeReturnString(apiURL,method, param);
        }
        byte[] data = HttpUtils.executeReturnByte(apiURL, method, param);
        if(BYTE_ARRAY==callType){
            return data;
        }
        if(BYTE_INPUT_STREAM==callType){
            return ArrayUtils.byteArrayToInputStream(data);
        }
        throw new ServiceCallException(callType);
    }

    /**
     * 处理返回结果
     * @param method      代理方法
     * @param callResult  远程访问的结果
     * @param apiUrl      资源的完整URL
     * @return
     */
    public static Object resultProcess(Method method, Object callResult, String apiUrl){
        //封装String类型的返回结果
        if(callResult instanceof String){
            String resultStr= (String) callResult;
            Type returnClass=method.getGenericReturnType();
            if(returnClass!=void.class){
                if(returnClass==String.class){
                    return resultStr;
                }else{
                    try{
                        return webConfig.getJsonSerializationScheme().deserialization(returnClass,resultStr);
                    }catch (Exception e){
                        resultStr=resultStr.length()>=225?resultStr.substring(0,225):resultStr;
                        throw new JsonConversionException(apiUrl,method.getReturnType(),resultStr);
                    }
                }
            }
            return null;
        }else{
            //byte[] 或者 InputStream类型
            return callResult;
        }
    }

    /**
     * 判断当前代理方法是否是文件拉取类型的请求
     * @param method 当前代理方法
     * @return Y->true/N->false
     */
    public static boolean isFileRequest(Method method){
        Class<?> returnType = method.getReturnType();
        return returnType==byte[].class|| InputStream.class.isAssignableFrom(returnType);
    }

    /**
     * Mapping方法校验，判断某个方法是否为HTTP代理方法
     * @param method 待检验的方法
     */
    public static void mappingMethodCheck(Method method){
        if(!AnnotationUtils.isExistOrByArray(method, MappingAnalysis.MAPPING_ANNOTATIONS)){
            throw new NotMappingMethodException("该方法不是Mapping方法，无法执行代理！错误位置："+method);
        }
    }

    /**
     * 判断当前代理方法是否是文件上传类型的请求
     * @param params 参数列表
     * @return  Y->true/N->false
     */
    public static boolean isMultipartFileMap(Map<String,Object> params){
        if(Assert.isEmptyMap(params)){
            return false;
        }
        for(Map.Entry<String,Object> entry:params.entrySet()){
            Object paramValue = entry.getValue();
            if(paramValue instanceof File[]){
                return true;
            }
            if(paramValue instanceof MultipartFile[]){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取HTTP代理方法执行时需要使用的请求方法[GET/POST/DELETE/PUT/...]
     * @param method 代理方法
     * @return RequestMethod
     */
    public static RequestMethod getRequestMethod(Method method){
        return AnnotationUtils.strengthenGet(method, RequestMapping.class).get(0).method()[0];
    }

    /**
     * 获取代理方法的详细描述信息
     * @param method 代理方法
     * @return
     */
    public static MappingDetails getMappingDetails(Method method){
        MappingDetails md=new MappingDetails();
        Annotation mappingAnnotation = AnnotationUtils.getByArray(method, MappingAnalysis.MAPPING_ANNOTATIONS);
        md.ip= (String[]) AnnotationUtils.getValue(mappingAnnotation,"ip");
        md.value= (String) AnnotationUtils.getValue(mappingAnnotation,"value");
        md.ipSection= (String[]) AnnotationUtils.getValue(mappingAnnotation,"ipSection");
        md.method=AnnotationUtils.strengthenGet(method, RequestMapping.class).get(0).method();
        return md;
    }

    /**
     * 请求类型当前代理方法的请求类型[文件请求/字符串请求/byte[]请求/InputStream请求]
     * @param method 代理方法
     * @param param  参数列表
     * @return
     */
    public static int getCallType(Method method,Map<String,Object> param){
        if(isMultipartFileMap(param)){
            return MULTIPART_FILE;
        }
        Class<?> returnType = method.getReturnType();
        if(returnType==byte[].class){
            return BYTE_ARRAY;
        }
        if(InputStream.class.isAssignableFrom(returnType)){
            return BYTE_INPUT_STREAM;
        }
        return STRING;
    }


    /**
     * 获取并封装请求远程接口的参数
     * @param method 代理方法
     * @param params 代理方法的执行参数
     * @return
     * @throws IOException
     * @throws IllegalAccessException
     */
    public static Map<String,Object> getParamMap(Method method,Object[] params) throws IOException, IllegalAccessException {
        Map<String,Object> callapiMap=new HashMap<>();
        Parameter[] parameters=method.getParameters();
        List<String> paramName= ASMUtil.getInterfaceMethodParamNames(method);
        String key=null;
        for(int i=0;i<parameters.length;i++) {
            if(AnnotationUtils.isExist(parameters[i], RequestBody.class)){
                Rest rest=parameters[i].getAnnotation(RequestBody.class).value();
                String serialization = webConfig.getJsonSerializationScheme().serialization(params[i]);
                callapiMap.put(UUID.randomUUID().toString(),new BodyObject(serialization,rest.getContentType()));
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

    //处理Rest参数的内部类
    public static class UrlAndParamMap{
        private static final String URL_PARAM_REGULAR="\\{[\\S\\s]+?\\}";
        private String url;
        private Map<String,Object> paramMap;

        public String getUrl() {
            return url;
        }

        public Map<String, Object> getParamMap() {
            return paramMap;
        }

        public UrlAndParamMap(String url,Map<String,Object> paramMap){
            List<String> urlParams = Regular.getArrayByExpression(url, URL_PARAM_REGULAR);
            if (!Assert.isEmptyCollection(urlParams)) {
                String newURL=url;
                for (String urlParam : urlParams) {
                    String paramKey = urlParam.substring(1, urlParam.length() - 1);
                    Object urlParamValue = paramMap.get(paramKey);
                    Assert.notNull(urlParamValue, String.format("`%s`中的`%s`参数没有对应的参数值！",url,urlParam));
                    newURL = newURL.replace(urlParam, urlParamValue.toString());
                    paramMap.remove(paramKey);
                }
                url=newURL;
            }
            this.url=url;
            this.paramMap=paramMap;
        }
    }
}
