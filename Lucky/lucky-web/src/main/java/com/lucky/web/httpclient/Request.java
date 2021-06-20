package com.lucky.web.httpclient;

import com.lucky.utils.regula.Regular;
import com.lucky.utils.serializable.json.LSON;
import com.lucky.web.core.BodyObject;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.webfile.MultipartFile;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/10 上午12:11
 */
public class Request {


    /**{}*/
    public static final String $_$="\\{[\\S\\s]+?\\}";
    /** 完整URL*/
    private String curl;
    /** 请求的地址*/
    private String url;
    /** 请求的方式*/
    private RequestMethod requestMethod;
    /** 请求的参数*/
    private Map<String,Object> requestParams = new HashMap<>();
    /** rest风格参数，在URL中的存在的参数*/
    private Map<String,Object> restParams = new HashMap<>();
    /** 配置信息*/
    private RequestConfig.Builder builder;
    /** 请求的头部信息*/
    private final RequestHeaderManage headerManage = new RequestHeaderManage();
    private static final String BODY_PARAM_NAME = "[@#]_BODY_[LUCKY]";

    Request(String url, RequestMethod requestMethod, Map<String, Object> requestParams) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.requestParams = requestParams;
    }

    private Request(String url, RequestMethod requestMethod) {
        this.url = url;
        this.requestMethod = requestMethod;
    }

    private Request(String url){
        this.url = url;
    }

    public static Request builder(String url,Object...urlParamValues){
        url = initRestParamByArray(url,urlParamValues);
        return new Request(url);
    }

    public static Request get(String url,Object...urlParamValues){
        url = initRestParamByArray(url,urlParamValues);
        return new Request(url,RequestMethod.GET);
    }

    public static Request post(String url,Object...urlParamValues){
        url = initRestParamByArray(url,urlParamValues);
        return new Request(url,RequestMethod.POST);
    }

    public static Request put(String url,Object...urlParamValues){
        url = initRestParamByArray(url,urlParamValues);
        return new Request(url,RequestMethod.PUT);
    }

    public static Request delete(String url,Object...urlParamValues){
        url = initRestParamByArray(url,urlParamValues);
        return new Request(url,RequestMethod.DELETE);
    }

    /**
     * 激活配置
     * @return 配置构建器
     */
    public RequestConfig.Builder activateConf(){
        builder = RequestConfig.custom();
        return builder;
    }

    /**
     * 获取当前配置构建器
     * @return 配置构建器
     */
    RequestConfig.Builder getConfigBuilder() {
        return builder;
    }

    /**
     * 获取请求目标的Url
     */
    String getUrl() {
        if(curl == null){
            curl = initRestParamByMap(url,restParams);
        }
        return curl;
    }

    /**
     * 设置请求目标的Url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取请求方法
     */
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    void setRequestMethod(RequestMethod requestMethod){
        this.requestMethod = requestMethod;
    }

    /**
     * 获取所有的请求参数
     */
    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    /**
     * 设置一组Rest请求参数
     * @param restParams Rest请求参数
     */
    public void setRestParams(Map<String, Object> restParams){
        this.restParams = restParams;
    }

    /**
     * 添加一个Rest请求参数
     * @param name  参数名
     * @param restValue 参数值
     */
    public void addRestParam(String name,Object restValue){
        restParams.put(name, restValue);
    }

    /**
     * 设置一组请求参数
     * @param requestParams 请求参数
     */
    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }

    /**
     * 添加一个请求参数
     * @param paramName  参数名
     * @param paramValue 参数值
     */
    public void addRequestParam(String paramName,Object paramValue){
        if(paramValue instanceof BodyObject){
            paramName = BODY_PARAM_NAME;
        }
        requestParams.put(paramName, paramValue);
    }

    /**
     * 添加一个文件类型请求参数
     * @param name  参数名
     * @param files 文件
     */
    public void addFileParam(String name, File...files){
        addRequestParam(name,files);
    }

    /**
     * 添加一个文件类型请求参数
     * @param name  参数名
     * @param files 文件
     */
    public void addMultipartFileParam(String name,MultipartFile...files){
        addRequestParam(name,files);
    }

    /**
     * 添加JSON类型的请求体参数
     * @param jsonStr json字符串
     */
    public void addJsonBody(String jsonStr){
        if(!LSON.validate(jsonStr)) throw new RuntimeException(jsonStr+" 不是json字符串！");
        BodyObject body = new BodyObject(jsonStr,Type.JSON.getContentType());
        addRequestParam(BODY_PARAM_NAME,body);
    }

    /**
     * 添加JSON类型的请求体参数
     * @param entity 实体
     */
    public void addJsonBody(Object entity){
        BodyObject body = new BodyObject(new LSON().toJson(entity),Type.JSON.getContentType());
        addRequestParam(BODY_PARAM_NAME,body);
    }

    /**
     * 添加XML类型的请求体参数
     * @param xmlStr xml字符串
     */
    public void addXmlBody(String xmlStr){
        BodyObject body = new BodyObject(xmlStr,Type.XML.getContentType());
        addRequestParam(BODY_PARAM_NAME,body);
    }

    /**
     * 添加JSON类型的请求体参数
     * @param entity 实体
     */
    public void addXmlBody(Object entity){
        final String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        XStream xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
        BodyObject body = new BodyObject(HEAD+xstream.toXML(entity),Type.JSON.getContentType());
        addRequestParam(BODY_PARAM_NAME,body);
    }

    /**
     * 返回头信息管理器
     * @return 头信息管理器
     */
    public RequestHeaderManage getHeaderManage() {
        return headerManage;
    }

    /**
     * 添加一个头信息
     * @param name    名称
     * @param header  头信息
     */
    public void addHeader(String name,String header){
        headerManage.addHeader(name, header);
    }

    /**
     * 设置一个头信息
     * @param name    名称
     * @param header  头信息
     */
    public void setHeader(String name,String header){
        headerManage.setHeader(name, header);
    }

    /**
     * 添加基于Basic Auth方式的权限认证
     * @param username 用户名
     * @param password 密码
     */
    public void setAuthorization(String username,String password){
        String auth = username + ":" +password;
        byte[] encodeAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic "+new String(encodeAuth, StandardCharsets.UTF_8);
        addHeader(HttpHeaders.AUTHORIZATION,authHeader);
    }

    @Override
    public String toString(){
        String temp = "[%s]%s {%s} ";
        return String.format(temp,requestMethod,url,requestParams);
    }

    private static String initRestParamByArray(String restUrl,Object...params){
        if(params.length == 0){
            return restUrl;
        }
        List<String> restParamNames = Regular.getArrayByExpression(restUrl, $_$);
        int tempSize = restParamNames.size();
        int paramSize = params.length;
        if(tempSize != paramSize){
            throw new RuntimeException("url中的参数占位符的数量与提供参数的数量不一致！\nURL         : "
                    +restUrl+"\nRest Names  : "+restParamNames+"\nRest Values : "+ Arrays.toString(params));
        }
        int i = 0;
        for (String paramName : restParamNames) {
            restUrl=restUrl.replace(paramName,params[i++].toString());
        }
        return restUrl;
    }

    private static String initRestParamByMap(String restUrl , Map<String,Object> restParamMap){
        Set<String> restParamNames = new HashSet<>(Regular.getArrayByExpression(restUrl, $_$));
        Set<String> mapKeys = restParamMap.keySet();
        Set<String> nameTemps = restParamNames.stream().map(s -> s.substring(1, s.length() - 1)).collect(Collectors.toSet());
        nameTemps.removeAll(mapKeys);
        if(!nameTemps.isEmpty()){
            throw new RuntimeException("url中的参数占位符的数量与提供参数的数量不一致["+restParamNames.size()+","+mapKeys.size()+
                    "]！\nURL         : " +restUrl+"\nRest Names  : "+restParamNames+"\nRest Values : "+ mapKeys);
        }

        for (String restParamName : restParamNames) {
            String paramName = restParamName.substring(1,restParamName.length()-1);
            String temp = "\\{"+paramName+"\\}";
            restUrl = restUrl.replaceAll(temp,restParamMap.get(paramName).toString());
        }
        return restUrl;
    }
}
