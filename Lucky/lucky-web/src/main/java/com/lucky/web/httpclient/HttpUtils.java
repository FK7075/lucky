package com.lucky.web.httpclient;

import com.google.gson.Gson;
import com.lucky.utils.base.Assert;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.exception.HttpClientRequestException;
import com.lucky.web.exception.NotFindRequestException;
import com.lucky.web.core.BodyObject;
import com.lucky.web.webfile.MultipartFile;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpUtils {

    private static final Logger log= LoggerFactory.getLogger(HttpUtils.class);
    /** Web配置类*/
    private static final WebConfig webConfig = WebConfig.getWebConfig();
    private static final Map<String,Object> NULL_MAP = new HashMap<>();

    //-----------------------------------------------------------------------------
    //                                   GET
    //-----------------------------------------------------------------------------

    //------------------------------REST_ENTITY----------------------------------------

    // 执行一次REST GET 请求，将结果转化为实体
    public static <T> T restGet(Request request,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = getResponse(request);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST GET 请求，将结果转化为实体
    public static <T> T restGet(Request request,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = getResponse(request);
        return responseEntity.getEntityBody(entityType);
    }

    // 执行一次REST GET 请求，将结果转化为实体
    public static <T> T restGet(String url,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = getResponse(url);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST GET 请求，将结果转化为实体
    public static <T> T restGet(String url,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = getResponse(url);
        return responseEntity.getEntityBody(entityType);
    }

    // 执行一次REST GET 请求，将结果转化为实体
    public static <T> T restGet(String url,Map<String, Object> requestParams,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = getResponse(url, requestParams);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST GET 请求，将结果转化为实体
    public static <T> T restGet(String url,Map<String, Object> requestParams,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = getResponse(url, requestParams);
        return new Gson().fromJson(getString(url,requestParams),entityType);

    }

    //------------------------------RESPONSE_ENTITY----------------------------------------

    // 执行一次GET请求并返回ResponseEntity类型的结果
    public static ResponseEntity getResponse(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.GET);
        return executeReturnResponse(request);
    }

    // 执行一次GET请求并返回ResponseEntity类型的结果，当不需要设置任何请求头时使用
    public static ResponseEntity getResponse(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return getResponse(new Request(url,RequestMethod.GET,requestParams));
    }

    // 执行一次GET请求并返回ResponseEntity类型的结果，当不需要设置任何请求头和任何参数时使用
    public static ResponseEntity getResponse(String url) throws IOException, URISyntaxException {
        return getResponse(url,NULL_MAP);
    }

    //------------------------------STRING----------------------------------------

    // 执行一次GET请求并返回String类型的结果，当不需要设置任何请求头和任何参数时使用
    public static String getString(String url) throws IOException, URISyntaxException {
        return getString(url,NULL_MAP);
    }

    // 执行一次GET请求并返回String类型的结果，当不需要设置任何请求头时使用
    public static String getString(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return getString(new Request(url,RequestMethod.GET,requestParams));
    }

    // 执行一次GET请求并返回String类型的结果
    public static String getString(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.GET);
        return executeReturnString(request);
    }

    //------------------------------BYTE[]----------------------------------------

    // 执行一次GET请求并返回byte[]类型的结果，当不需要设置任何请求头和任何参数时使用
    public static byte[] getByte(String url) throws IOException, URISyntaxException {
        return getByte(url,NULL_MAP);
    }

    // 执行一次GET请求并返回byte[]类型的结果，当不需要设置任何请求头时使用
    public static byte[] getByte(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return getByte(new Request(url,RequestMethod.GET,requestParams));
    }

    // 执行一次GET请求并返回byte[]类型的结果
    public static byte[] getByte(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.GET);
        return executeReturnByte(request);
    }

    //------------------------------VOID----------------------------------------


    // 执行一次GET请求,不返回任何结果，当不需要设置任何请求头和任何参数时使用
    public static void get(String url) throws IOException, URISyntaxException {
        get(url,NULL_MAP);
    }

    // 执行一次GET请求,不返回任何结果，当不需要设置任何请求头时使用
    public static void get(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        get(new Request(url,RequestMethod.GET,requestParams));
    }

    // 执行一次GET请求,不返回任何结果
    public static void get(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.GET);
        execute(request);
    }


    //-----------------------------------------------------------------------------
    //                                   POST
    //-----------------------------------------------------------------------------

    //------------------------------REST_ENTITY----------------------------------------

    // 执行一次REST POST 请求，将结果转化为实体
    public static <T> T restPost(Request request,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = postResponse(request);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST POST 请求，将结果转化为实体
    public static <T> T restPost(Request request,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = postResponse(request);
        return responseEntity.getEntityBody(entityType);

    }

    // 执行一次REST POST 请求，将结果转化为实体
    public static <T> T restPost(String url,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = postResponse(url);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST POST 请求，将结果转化为实体
    public static <T> T restPost(String url,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = postResponse(url);
        return responseEntity.getEntityBody(entityType);
    }

    // 执行一次REST POST 请求，将结果转化为实体
    public static <T> T restPost(String url,Map<String, Object> requestParams,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = postResponse(url, requestParams);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST POST 请求，将结果转化为实体
    public static <T> T restPost(String url,Map<String, Object> requestParams,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = postResponse(url, requestParams);
        return responseEntity.getEntityBody(entityType);
    }

    //------------------------------RESPONSE_ENTITY----------------------------------------

    // 执行一次POST请求并返回ResponseEntity类型的结果
    public static ResponseEntity postResponse(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.POST);
        return executeReturnResponse(request);
    }

    // 执行一次POST请求并返回ResponseEntity类型的结果，当不需要设置任何请求头时使用
    public static ResponseEntity postResponse(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return postResponse(new Request(url,RequestMethod.POST,requestParams));
    }

    // 执行一次POST请求并返回ResponseEntity类型的结果，当不需要设置任何请求头和任何参数时使用
    public static ResponseEntity postResponse(String url) throws IOException, URISyntaxException {
        return postResponse(url,NULL_MAP);
    }

    //------------------------------STRING----------------------------------------

    // 执行一次POST请求并返回String类型的结果，当不需要设置任何请求头和任何参数时使用
    public static String postString(String url) throws IOException, URISyntaxException {
        return postString(url,NULL_MAP);
    }

    // 执行一次POST请求并返回String类型的结果，当不需要设置任何请求头时使用
    public static String postString(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return postString(new Request(url,RequestMethod.POST,requestParams));
    }

    // 执行一次POST请求并返回String类型的结果
    public static String postString(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.POST);
        return executeReturnString(request);
    }

    //------------------------------BYTE[]----------------------------------------

    // 执行一次POST请求并返回byte[]类型的结果，当不需要设置任何请求头和任何参数时使用
    public static byte[] postByte(String url) throws IOException, URISyntaxException {
        return postByte(url,NULL_MAP);
    }

    // 执行一次POST请求并返回byte[]类型的结果，当不需要设置任何请求头时使用
    public static byte[] postByte(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return postByte(new Request(url,RequestMethod.POST,requestParams));
    }

    // 执行一次POST请求并返回byte[]类型的结果
    public static byte[] postByte(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.POST);
        return executeReturnByte(request);
    }

    //------------------------------VOID----------------------------------------


    // 执行一次POST请求,不返回任何结果，当不需要设置任何请求头和任何参数时使用
    public static void post(String url) throws IOException, URISyntaxException {
        post(url,NULL_MAP);
    }

    // 执行一次POST请求,不返回任何结果，当不需要设置任何请求头时使用
    public static void post(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        post(new Request(url,RequestMethod.POST,requestParams));
    }

    // 执行一次POST请求,不返回任何结果
    public static void post(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.POST);
        execute(request);
    }

    //-----------------------------------------------------------------------------
    //                                   PUT
    //-----------------------------------------------------------------------------

    //------------------------------REST_ENTITY----------------------------------------

    // 执行一次REST PUT 请求，将结果转化为实体
    public static <T> T restPut(Request request,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = putResponse(request);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST PUT 请求，将结果转化为实体
    public static <T> T restPut(Request request,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = putResponse(request);
        return responseEntity.getEntityBody(entityType);
    }

    // 执行一次REST PUT 请求，将结果转化为实体
    public static <T> T restPut(String url,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = postResponse(url);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST PUT 请求，将结果转化为实体
    public static <T> T restPut(String url,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = putResponse(url);
        return responseEntity.getEntityBody(entityType);
    }

    // 执行一次REST PUT 请求，将结果转化为实体
    public static <T> T restPut(String url,Map<String, Object> requestParams,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = putResponse(url, requestParams);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST PUT 请求，将结果转化为实体
    public static <T> T restPut(String url,Map<String, Object> requestParams,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = putResponse(url, requestParams);
        return responseEntity.getEntityBody(entityType);
    }

    //------------------------------RESPONSE_ENTITY----------------------------------------

    // 执行一次PUT请求并返回ResponseEntity类型的结果
    public static ResponseEntity putResponse(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.PUT);
        return executeReturnResponse(request);
    }

    // 执行一次PUT请求并返回ResponseEntity类型的结果，当不需要设置任何请求头时使用
    public static ResponseEntity putResponse(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return putResponse(new Request(url,RequestMethod.PUT,requestParams));
    }

    // 执行一次PUT请求并返回ResponseEntity类型的结果，当不需要设置任何请求头和任何参数时使用
    public static ResponseEntity putResponse(String url) throws IOException, URISyntaxException {
        return putResponse(url,NULL_MAP);
    }

    //------------------------------STRING----------------------------------------

    // 执行一次PUT请求并返回String类型的结果，当不需要设置任何请求头和任何参数时使用
    public static String putString(String url) throws IOException, URISyntaxException {
        return putString(url,NULL_MAP);
    }

    // 执行一次PUT请求并返回String类型的结果，当不需要设置任何请求头时使用
    public static String putString(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return putString(new Request(url,RequestMethod.PUT,requestParams));
    }

    // 执行一次PUT请求并返回String类型的结果
    public static String putString(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.PUT);
        return executeReturnString(request);
    }

    //------------------------------BYTE[]----------------------------------------

    // 执行一次PUT请求并返回byte[]类型的结果，当不需要设置任何请求头和任何参数时使用
    public static byte[] putByte(String url) throws IOException, URISyntaxException {
        return putByte(url,NULL_MAP);
    }

    // 执行一次PUT请求并返回byte[]类型的结果，当不需要设置任何请求头时使用
    public static byte[] putByte(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return putByte(new Request(url,RequestMethod.PUT,requestParams));
    }

    // 执行一次PUT请求并返回byte[]类型的结果
    public static byte[] putByte(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.PUT);
        return executeReturnByte(request);
    }

    //------------------------------VOID----------------------------------------

    // 执行一次PUT请求,不返回任何结果，当不需要设置任何请求头和任何参数时使用
    public static void put(String url) throws IOException, URISyntaxException {
        put(url,NULL_MAP);
    }

    // 执行一次PUT请求,不返回任何结果，当不需要设置任何请求头时使用
    public static void put(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        put(new Request(url,RequestMethod.PUT,requestParams));
    }

    // 执行一次PUT请求,不返回任何结果
    public static void put(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.PUT);
        execute(request);
    }


    //-----------------------------------------------------------------------------
    //                                   DELETE
    //-----------------------------------------------------------------------------


    //------------------------------REST_ENTITY----------------------------------------

    // 执行一次REST DELETE 请求，将结果转化为实体
    public static <T> T restDelete(Request request,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = deleteResponse(request);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST DELETE 请求，将结果转化为实体
    public static <T> T restDelete(Request request,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = deleteResponse(request);
        return responseEntity.getEntityBody(entityType);
    }

    // 执行一次REST DELETE 请求，将结果转化为实体
    public static <T> T restDelete(String url,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = deleteResponse(url);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST DELETE 请求，将结果转化为实体
    public static <T> T restDelete(String url,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = deleteResponse(url);
        return responseEntity.getEntityBody(entityType);
    }

    // 执行一次REST DELETE 请求，将结果转化为实体
    public static <T> T restDelete(String url,Map<String, Object> requestParams,Class<T> entityClass) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = deleteResponse(url,requestParams);
        return responseEntity.getEntityBody(entityClass);
    }

    // 执行一次REST DELETE 请求，将结果转化为实体
    public static <T> T restDelete(String url,Map<String, Object> requestParams,java.lang.reflect.Type entityType) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = deleteResponse(url,requestParams);
        return responseEntity.getEntityBody(entityType);
    }

    //------------------------------RESPONSE_ENTITY----------------------------------------

    // 执行一次DELETE请求并返回ResponseEntity类型的结果
    public static ResponseEntity deleteResponse(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.GET);
        return executeReturnResponse(request);
    }

    // 执行一次DELETE请求并返回ResponseEntity类型的结果，当不需要设置任何请求头时使用
    public static ResponseEntity deleteResponse(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return deleteResponse(new Request(url,RequestMethod.GET,requestParams));
    }

    // 执行一次DELETE请求并返回ResponseEntity类型的结果，当不需要设置任何请求头和任何参数时使用
    public static ResponseEntity deleteResponse(String url) throws IOException, URISyntaxException {
        return deleteResponse(url,NULL_MAP);
    }

    //------------------------------STRING----------------------------------------

    // 执行一次DELETE请求并返回String类型的结果，当不需要设置任何请求头和任何参数时使用
    public static String deleteString(String url) throws IOException, URISyntaxException {
        return deleteString(url,NULL_MAP);
    }

    // 执行一次DELETE请求并返回String类型的结果，当不需要设置任何请求头时使用
    public static String deleteString(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return deleteString(new Request(url,RequestMethod.DELETE,requestParams));
    }

    // 执行一次DELETE请求并返回String类型的结果
    public static String deleteString(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.DELETE);
        return executeReturnString(request);
    }

    //------------------------------BYTE[]----------------------------------------

    // 执行一次DELETE请求并返回byte[]类型的结果，当不需要设置任何请求头和任何参数时使用
    public static byte[] deleteByte(String url) throws IOException, URISyntaxException {
        return deleteByte(url,NULL_MAP);
    }

    // 执行一次DELETE请求并返回byte[]类型的结果，当不需要设置任何请求头时使用
    public static byte[] deleteByte(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return deleteByte(new Request(url,RequestMethod.DELETE,requestParams));
    }

    // 执行一次DELETE请求并返回byte[]类型的结果
    public static byte[] deleteByte(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.DELETE);
        return executeReturnByte(request);
    }

    //------------------------------VOID----------------------------------------

    // 执行一次DELETE请求,不返回任何结果，当不需要设置任何请求头和任何参数时使用
    public static void delete(String url) throws IOException, URISyntaxException {
        delete(url,NULL_MAP);
    }

    // 执行一次DELETE请求,不返回任何结果，当不需要设置任何请求头时使用
    public static void delete(String url,Map<String, Object> requestParams) throws IOException, URISyntaxException {
        delete(new Request(url,RequestMethod.DELETE,requestParams));
    }

    // 执行一次DELETE请求,不返回任何结果
    public static void delete(Request request) throws IOException, URISyntaxException {
        request.setRequestMethod(RequestMethod.DELETE);
        execute(request);
    }



    //-----------------------------------------------------------------------------
    //                                   BASE
    //-----------------------------------------------------------------------------

    // 执行一次HTTP请求并返回String类型的结果，当不需要设置任何请求头时使用
    public static String executeReturnString(String url,RequestMethod requestMethod, Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return executeReturnString(new Request(url, requestMethod, requestParams));
    }

    // 执行一次HTTP请求并返回String类型的结果
    public static String executeReturnString(Request request) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = executeReturnResponse(request);
        String result = responseEntity.getStringBody();
        Header[] responseHeaders = responseEntity.getAllHeader();
        if(responseEntity.getCode() == 200){
            log.debug("[{}]{} => Header<{}> Boyd<{}>",request.getRequestMethod(),request.getUrl(),Arrays.toString(responseHeaders),result);
            return result;
        }
        log.error("[{}]{} => Header<{}> Boyd<{}>",request.getRequestMethod(),request.getUrl(),Arrays.toString(responseHeaders),result);
        log.error("远程服务异常，未能正常响应..");
        throw new HttpClientRequestException("远程服务异常，访问失败");
    }

    // 执行一次HTTP请求并返回byte[]类型的结果，当不需要设置任何请求头时使用
    public static byte[] executeReturnByte(String url,RequestMethod requestMethod, Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return executeReturnByte(new Request(url, requestMethod, requestParams));
    }

    // 执行一次HTTP请求并返回byte[]类型的结果
    public static byte[] executeReturnByte(Request request) throws IOException, URISyntaxException {
        ResponseEntity responseEntity = executeReturnResponse(request);
        if(responseEntity.getCode() == 200){
            log.debug("[{}]{}>",request.getRequestMethod(),request.getUrl());
            return responseEntity.getByteBody();
        }
        log.error("[{}]{} => Header<{}> Boyd<{}>",request.getRequestMethod(),request.getUrl(),Arrays.toString(responseEntity.getAllHeader()),responseEntity.getStringBody());
        log.error("远程服务异常，未能正常响应..");
        throw new HttpClientRequestException("远程服务异常，访问失败");
    }

    // 执行一次HTTP请求并返回ResponseEntity类型的结果，当不需要设置任何请求头时使用
    public static ResponseEntity executeReturnResponse(String url,RequestMethod requestMethod, Map<String, Object> requestParams) throws IOException, URISyntaxException {
        return executeReturnResponse(new Request(url, requestMethod, requestParams));
    }

    // 执行一次HTTP请求并返回ResponseEntity类型的结果
    public static ResponseEntity executeReturnResponse(Request request) throws IOException, URISyntaxException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpResponse response = execute(client, request);
            return new ResponseEntity(response);
        }
    }

    // 执行一次HTTP请求,不返回任何结果
    public static void execute(Request request) throws IOException, URISyntaxException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            execute(client,request);
        }
    }

    // 执行一次HTTP请求,不返回任何结果，当不需要设置任何请求头时使用
    public static void execute(String url,RequestMethod requestMethod, Map<String, Object> requestParams) throws IOException, URISyntaxException{
        execute(new Request(url,requestMethod,requestParams));
    }


    /**
     * 执行一次HTTP请求
     * @param client        HTTP客户端
     * @param request       请求信息
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static HttpResponse execute(CloseableHttpClient client,Request request) throws IOException, URISyntaxException {
        HttpRequestBase method = getHttpRequestObject(request.getUrl(), request.getRequestParams(), request.getRequestMethod());
        request.getHeaderManage().initHeader(method);
        RequestConfig.Builder configBuilder = request.getConfigBuilder();
        RequestConfig config = configBuilder == null ? getRequestConfig() : configBuilder.build();
        method.setConfig(config);
        return client.execute(method);
    }

    /**
     * 得到Request的配置对对象
     * @return
     */
    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(webConfig.getConnectTimeout()).setConnectionRequestTimeout(webConfig.getRequestTimeout())
                .setSocketTimeout(webConfig.getSocketTimeout()).build();
    }

    /**
     * 得到HttpGet/HttpPost/HttpDelete/HttpPut对象
     * @param url           url地址
     * @param params        参数列表
     * @param requestMethod 请求类型
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static HttpRequestBase getHttpRequestObject(String url, Map<String, Object> params, RequestMethod requestMethod) throws IOException, URISyntaxException {
        if (requestMethod == RequestMethod.GET) {
            HttpGet get;
            if (Assert.isEmptyMap(params)) {
                get=new HttpGet(url);
            } else {
                URIBuilder builder = new URIBuilder(url);
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key).toString());
                }
                get=new HttpGet(builder.build());
            }
            get.addHeader(HttpHeaders.CONTENT_TYPE, Type.FROM_KV.getContentType());
            return get;
        } else if (requestMethod == RequestMethod.DELETE) {
            HttpDelete method;
            if (Assert.isEmptyMap(params)) {
                method=new HttpDelete(url);
            } else {
                URIBuilder builder = new URIBuilder(url);
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key).toString());
                }
                method=new HttpDelete(builder.build());
            }
            method.addHeader(HttpHeaders.CONTENT_TYPE, Type.FROM_KV.getContentType());
            return method;
        } else if (requestMethod == RequestMethod.POST) {

            HttpPost post;
            //无参数请求
            if (Assert.isEmptyMap(params)) {
                post=new HttpPost(url);
                post.addHeader(HttpHeaders.CONTENT_TYPE,  Type.FROM_KV.getContentType());
                return post;
            }

            //文件类型参数请求
            if(isFileRequest(params)){
                post = new HttpPost(url);
                post.setEntity(getFileHttpEntity(params));
                return post;
            }

            //键值对类型的参数请求
            BodyObject bodyObject = getBodyObject(params);
            if(Assert.isNull(bodyObject)){
                post = new HttpPost(url);
                post.setEntity(getUrlEncodedFormEntity(params));
                post.addHeader(HttpHeaders.CONTENT_TYPE,  Type.FROM_KV.getContentType());
                return post;
            }

            //请求体参数类型请求
            post=new HttpPost(getUrl(url,params));
            post.addHeader(HttpHeaders.CONTENT_TYPE,  bodyObject.getContentType());
            post.setEntity(getBodyHttpEntity(bodyObject));
            return post;

        } else if (requestMethod == RequestMethod.PUT) {
            HttpPut put;

            //无参数请求
            if (Assert.isEmptyMap(params)) {
                put=new HttpPut(url);
                put.addHeader(HttpHeaders.CONTENT_TYPE,  Type.FROM_KV.getContentType());
                return put;
            }

            //文件类型参数请求
            if(isFileRequest(params)){
                put = new HttpPut(url);
                put.setEntity(getFileHttpEntity(params));
                return put;
            }

            //键值对类型的参数请求
            BodyObject bodyObject = getBodyObject(params);
            if(Assert.isNull(bodyObject)){
                put = new HttpPut(url);
                put.setEntity(getUrlEncodedFormEntity(params));
                put.addHeader(HttpHeaders.CONTENT_TYPE,  Type.FROM_KV.getContentType());
                return put;
            }

            //请求体参数类型请求
            put=new HttpPut(getUrl(url,params));
            put.addHeader(HttpHeaders.CONTENT_TYPE,  bodyObject.getContentType());
            put.setEntity(getBodyHttpEntity(bodyObject));
            return put;
        } else {
            log.error("Lucky目前不支持该请求 [-" + requestMethod + "-]");
            throw new NotFindRequestException("Lucky目前不支持该请求 [-" + requestMethod + "-]");
        }
    }


    /**
     * 得到POST、PUT请求的参数
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private static UrlEncodedFormEntity getUrlEncodedFormEntity(Map<String, Object> params) throws UnsupportedEncodingException {
        List<BasicNameValuePair> list = new ArrayList<>();
        for (String key : params.keySet()) {
            list.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        return new UrlEncodedFormEntity(list);
    }

    /**
     * 判断是否为文件类型的请求
     * @param params 参数列表
     * @return
     */
    private static boolean isFileRequest(Map<String,Object> params){
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Class<?> requestParamType = entry.getValue().getClass();
            if(File.class== requestParamType          ||
               File[].class == requestParamType       ||
               MultipartFile.class == requestParamType||
               MultipartFile[].class == requestParamType){
                return true;
            }
        }
        return false;
    }

    /**
     * 包装文件类型的参数
     * @param multipartFileParams 文件类型的参数
     * @return
     * @throws FileNotFoundException
     */
    private static HttpEntity getFileHttpEntity(Map<String,Object> multipartFileParams) throws FileNotFoundException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//加上此行代码解决返回中文乱码问题
        for (Map.Entry<String, Object> e : multipartFileParams.entrySet()) {
            Class<?> paramValueClass = e.getValue().getClass();
            //包装File类型的参数
            if (File.class == paramValueClass) {
                File file = (File) e.getValue();
                builder.addBinaryBody(e.getKey(), new FileInputStream(file), ContentType.MULTIPART_FORM_DATA, file.getName());//文件参数-File
            }
            //包装File[]类型的参数
            else if (File[].class == paramValueClass) {
                File[] files = (File[]) e.getValue();
                for (File file : files) {
                    builder.addBinaryBody(e.getKey(), new FileInputStream(file), ContentType.MULTIPART_FORM_DATA, file.getName());//文件参数-File[]
                }
            }
            //包装MultipartFile类型的参数
            else if (MultipartFile.class == paramValueClass) {
                MultipartFile mf = (MultipartFile) e.getValue();
                builder.addBinaryBody(e.getKey(), mf.getInputStream(), ContentType.MULTIPART_FORM_DATA, mf.getFileName());//文件参数-MultipartFile
            }
            //包装MultipartFile[]类型的参数
            else if (MultipartFile[].class == paramValueClass) {
                MultipartFile[] mfs = (MultipartFile[]) e.getValue();
                for (MultipartFile mf : mfs) {
                    builder.addBinaryBody(e.getKey(), mf.getInputStream(), ContentType.MULTIPART_FORM_DATA, mf.getFileName());//文件参数-MultipartFile[]
                }
            }
            //其他类型将会被当做String类型的参数
            else {
                builder.addTextBody(e.getKey(), e.getValue().toString(), ContentType.APPLICATION_JSON);// 设置请求中String类型的参数
            }

        }
        return builder.build();
    }

    /**
     * 包装请求体中的参数
     * @param bodyObject 请求体中的参数
     * @return
     */
    private static HttpEntity getBodyHttpEntity(BodyObject bodyObject){
        return  new StringEntity(bodyObject.getBodyObject(),"UTF-8");
    }

    /***
     * 找到参数列表中被@RequestBody注解标注的JSON
     * @param param 本次请求的参数列表
     * @return
     */
    private static BodyObject getBodyObject(Map<String,Object> param){
        Collection<Object> values = param.values();
        for (Object value : values) {
            if(value instanceof BodyObject){
                return (BodyObject)value;
            }
        }
        return null;
    }

    /**
     * 过滤掉参数列表中的JSONObject,并将其他的参数直接拼接到URL上
     * @param url 预处理的URL
     * @param param 可能包含JSONObject的参数列表
     * @return 将参数列表中的参数直接拼接到URL，返回拼接后的URL
     */
    private static String getUrl(String url,Map<String,Object> param){
        if(Assert.isEmptyMap(param)){
            return url;
        }
        StringBuilder params=new StringBuilder();
        for(Map.Entry<String,Object> entry:param.entrySet()){
            if(BodyObject.class.equals(entry.getValue().getClass())){
                continue;
            }
            params.append(entry.getKey()).append("=").append(entry.getValue().toString()).append("&");
        }
        String paramStr = params.toString();
        paramStr=paramStr.endsWith("&")?paramStr.substring(0,paramStr.length()-1):paramStr;
        if(url.contains("?")){
            return url+"&"+paramStr;
        }
        return url+"?"+paramStr;
    }

}
