package com.lucky.web.conf;

import com.lucky.framework.confanalysis.LuckyConfig;
import com.lucky.framework.serializable.JSONSerializationScheme;
import com.lucky.framework.serializable.XMLSerializationScheme;
import com.lucky.framework.serializable.implement.GsonSerializationScheme;
import com.lucky.framework.serializable.implement.XtreamSerializationScheme;
import com.lucky.utils.base.Assert;
import com.lucky.web.core.*;
import com.lucky.web.core.parameter.ParameterAnalysis;
import com.lucky.web.core.parameter.ParameterAnalysisChain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Web层的相关配置
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 9:58
 */
public class WebConfig extends LuckyConfig {

    private static WebConfig webConfig;

    /** 请求参数编码格式*/
    private String encoding;
    /** 设置单个文件大小为限制1M(单位：kb)*/
    private long multipartMaxFileSize;
    /** 总上传的数据大小也为10M(单位：kb)*/
    private long multipartMaxRequestSize;
    /**静态资源映射配置*/
    private Map<String,String> staticHander;
    /** 转发或重定向的前缀*/
    private String prefix;
    /** 转发或重定向的后缀*/
    private String suffix;
    /**是否开启Lucky的静态资源管理器*/
    private boolean openStaticResourceManage;
    /** favicon.ico的位置*/
    private String favicon;
    /** 静态文件的根目录*/
    private String webRoot;
    /** 是否开启post请求的请求类型转换*/
    private boolean postChangeMethod;
    /** 全局资源的IP限制*/
    private Set<String> globalResourcesIpRestrict;
    /** 静态资源的IP限制*/
    private Set<String> staticResourcesIpRestrict;
    /**指定资源的IP限制*/
    private Map<String,Set<String>> specifiResourcesIpRestrict;
    /** 远程API的访问路径*/
    private Map<String,String> callApi;
    /**连接超时时间*/
    private int connectTimeout;
    /**连接请求超时时间*/
    private int requestTimeout;
    /**socket超时时间*/
    private int socketTimeout;
    /**错误页面配置*/
    private Map<String,String> errorPage;
    /** XML序列化方案*/
    private XMLSerializationScheme xmlSerializationScheme;
    /** JSON序列化方案*/
    private JSONSerializationScheme jsonSerializationScheme;
    /** 映射的预处理器*/
    private MappingPreprocess mappingPreprocess;
    /** 参数解析链*/
    private ParameterAnalysisChain parameterAnalysisChain;
    /** 对参数的二次处理[加密、校验等操作]*/
    private ParameterProcess parameterProcess;
    /** 定义转发与重定向*/
    private LuckyResponse response;

    public String getEncoding() {
        return encoding;
    }

    public MappingPreprocess getMappingPreprocess() {
        return mappingPreprocess;
    }

    public void setMappingPreprocess(MappingPreprocess mappingPreprocess) {
        this.mappingPreprocess = mappingPreprocess;
    }

    public long getMultipartMaxFileSize() {
        return multipartMaxFileSize;
    }

    public long getMultipartMaxRequestSize() {
        return multipartMaxRequestSize;
    }

    public Map<String, String> getStaticHander() {
        return staticHander;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isOpenStaticResourceManage() {
        return openStaticResourceManage;
    }

    public String getWebRoot() {
        return webRoot;
    }

    public boolean isPostChangeMethod() {
        return postChangeMethod;
    }

    public Set<String> getGlobalResourcesIpRestrict() {
        return globalResourcesIpRestrict;
    }

    public Set<String> getStaticResourcesIpRestrict() {
        return staticResourcesIpRestrict;
    }

    public Map<String, Set<String>> getSpecifiResourcesIpRestrict() {
        return specifiResourcesIpRestrict;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public XMLSerializationScheme getXmlSerializationScheme() {
        return xmlSerializationScheme;
    }

    public JSONSerializationScheme getJsonSerializationScheme() {
        return jsonSerializationScheme;
    }

    public Map<String, String> getCallApi() {
        return callApi;
    }

    public void setCallApi(Map<String, String> callApi) {
        this.callApi = callApi;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setMultipartMaxFileSize(long multipartMaxFileSize) {
        this.multipartMaxFileSize = multipartMaxFileSize;
    }

    public void setMultipartMaxRequestSize(long multipartMaxRequestSize) {
        this.multipartMaxRequestSize = multipartMaxRequestSize;
    }

    public void setStaticHander(Map<String, String> staticHander) {
        this.staticHander = staticHander;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setOpenStaticResourceManage(boolean openStaticResourceManage) {
        this.openStaticResourceManage = openStaticResourceManage;
    }

    public void setWebRoot(String webRoot) {
        this.webRoot = webRoot;
    }

    public void setPostChangeMethod(boolean postChangeMethod) {
        this.postChangeMethod = postChangeMethod;
    }

    public void setGlobalResourcesIpRestrict(Set<String> globalResourcesIpRestrict) {
        this.globalResourcesIpRestrict = globalResourcesIpRestrict;
    }

    public void setStaticResourcesIpRestrict(Set<String> staticResourcesIpRestrict) {
        this.staticResourcesIpRestrict = staticResourcesIpRestrict;
    }

    public void setSpecifiResourcesIpRestrict(Map<String, Set<String>> specifiResourcesIpRestrict) {
        this.specifiResourcesIpRestrict = specifiResourcesIpRestrict;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setXmlSerializationScheme(XMLSerializationScheme xmlSerializationScheme) {
        this.xmlSerializationScheme = xmlSerializationScheme;
    }

    public void setJsonSerializationScheme(JSONSerializationScheme jsonSerializationScheme) {
        this.jsonSerializationScheme = jsonSerializationScheme;
    }

    public Map<String, String> getErrorPage() {
        return errorPage;
    }

    public ParameterAnalysisChain getParameterAnalysisChain() {
        return parameterAnalysisChain;
    }

    public void addParameterAnalysis(ParameterAnalysis parameterAnalysis) {
        this.parameterAnalysisChain.addParameterAnalysis(parameterAnalysis);
    }

    public void setErrorPage(Map<String, String> errorPage) {
        this.errorPage = errorPage;
    }

    public void addAllErrorPage(Map<String, String> errorPage){
        if(!Assert.isEmptyMap(errorPage)){
            for(Map.Entry<String,String> entry:errorPage.entrySet()){
                this.errorPage.put(entry.getKey(),entry.getValue());
            }
        }
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public LuckyResponse getResponse() {
        return response;
    }

    public void setResponse(LuckyResponse response) {
        this.response = response;
    }

    private WebConfig() {
        parameterAnalysisChain=new ParameterAnalysisChain();
    }

    public ParameterProcess getParameterProcess() {
        return parameterProcess;
    }

    public void setParameterProcess(ParameterProcess parameterProcess) {
        this.parameterProcess = parameterProcess;
    }

    private static void defaultInit(WebConfig conf){
        conf.setEncoding("UTF-8");
        conf.setWebRoot("classpath:/templates/");
        conf.setOpenStaticResourceManage(false);
        conf.setPostChangeMethod(false);
        conf.setMultipartMaxFileSize(1*1024);
        conf.setMultipartMaxRequestSize(10*1024);
        conf.setXmlSerializationScheme(new XtreamSerializationScheme());
        conf.setJsonSerializationScheme(new GsonSerializationScheme());
        conf.setMappingPreprocess(new DefaultMappingPreprocess());
        conf.setResponse(new DefaultLuckyResponse());
        conf.setParameterProcess(new ParameterProcess());
        conf.setPrefix("");
        conf.setSuffix("");
        conf.setConnectTimeout(5000);
        conf.setRequestTimeout(5000);
        conf.setSocketTimeout(5000);
        Map<String,String> errorPathMap=new HashMap<>();
        errorPathMap.put("404","/lucky-web/404.html");
        errorPathMap.put("403","/lucky-web/404.html");
        errorPathMap.put("500","/lucky-web/500.html");
        conf.setErrorPage(errorPathMap);
        conf.setFavicon("/lucky-web/favicon.ico");
        conf.setStaticHander(new HashMap<>());
        conf.setCallApi(new HashMap<>());
        conf.setGlobalResourcesIpRestrict(new HashSet<>());
        conf.setStaticResourcesIpRestrict(new HashSet<>());
        conf.setSpecifiResourcesIpRestrict(new HashMap<>());
        conf.setFirst(true);
    }

    public static WebConfig defaultWebConfig() {
        if(webConfig==null){
            webConfig=new WebConfig();
            defaultInit(webConfig);
        }
        return webConfig;
    }

    public static WebConfig getWebConfig(){
        WebConfig webConfig = defaultWebConfig();
        if(webConfig.isFirst()){
            YamlParsing.loadWeb(webConfig);
        }
        return webConfig;
    }
}
