package com.lucky.web.conf;

import com.lucky.framework.serializable.JSONSerializationScheme;
import com.lucky.framework.serializable.XMLSerializationScheme;
import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.web.core.LuckyResponse;
import com.lucky.web.core.MappingPreprocess;
import com.lucky.web.core.parameter.analysis.ParameterAnalysis;
import com.lucky.web.core.parameter.enhance.ParameterEnhance;
import com.lucky.web.interceptor.HandlerInterceptor;
import com.lucky.web.interceptor.InterceptorRegistry;
import com.lucky.web.interceptor.PathAndInterceptor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 10:58
 */
public abstract class YamlParsing {

    private static YamlConfAnalysis yaml;
    private static final String CONF_PREFIX="lucky.web.";

    public static void loadWeb(WebConfig conf){
        yaml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static Object get(String suffix){
        return yaml.getObject(CONF_PREFIX+suffix);
    }

    private static void load(Map<String,Object> config,WebConfig web){
        if(config.containsKey("lucky")){
            Object luckyNode = config.get("lucky");
            if(luckyNode instanceof Map){
                Map<String,Object> luckyMap= (Map<String, Object>) luckyNode;
                if(luckyMap.containsKey("web")){
                    Object webNode = luckyMap.get("web");
                    if(webNode instanceof Map){
                        Map<String,Object> webMap= (Map<String, Object>) webNode;
                        if(webMap.containsKey("encoding")){
                            web.setEncoding((String)get("encoding"));
                        }
                        if(webMap.containsKey("web-root")){
                            web.setWebRoot((String)get("web-root"));
                        }
                        if(webMap.containsKey("post-change-method")){
                            web.setPostChangeMethod((boolean)get("post-change-method"));
                        }
                        if(webMap.containsKey("static-resource-manage")){
                            web.setOpenStaticResourceManage((boolean)get("static-resource-manage"));
                        }
                        if(webMap.containsKey("multipart-max-file-size")){
                            web.setMultipartMaxFileSize((int) JavaConversion.strToBasic(
                                    get("multipart-max-file-size").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("multipart-max-request-size")){
                            web.setMultipartMaxRequestSize((int) JavaConversion.strToBasic(
                                    get("multipart-max-request-size").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("prefix")){
                            web.setPrefix((String)get("prefix"));
                        }
                        if(webMap.containsKey("suffix")){
                            web.setSuffix((String)get("suffix"));
                        }
                        if(webMap.containsKey("httpclient-connection-timeout")){
                            web.setConnectTimeout((int)JavaConversion.strToBasic(
                                    get("httpclient-connection-timeout").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("httpclient-request-timeout")){
                            web.setRequestTimeout((int)JavaConversion.strToBasic(
                                    get("httpclient-request-timeout").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("httpclient-socket-timeout")){
                            web.setSocketTimeout((int)JavaConversion.strToBasic(
                                    get("httpclient-socket-timeout").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("interceptors")){
                            List<Map<String,Object>> interceptors= (List<Map<String, Object>>) webMap.get("interceptor");
                            for (Map<String, Object> interceptor : interceptors) {
                                PathAndInterceptor pi=new PathAndInterceptor();
                                Object interceptorClass = interceptor.get("class");
                                Object interceptorPriority = interceptor.get("priority");
                                Object interceptorPath = interceptor.get("path");
                                Object interceptorExcludePath = interceptor.get("exclude-path");
                                if(interceptorClass instanceof String){
                                    pi.setInterceptor((HandlerInterceptor)ClassUtils.newObject(get(interceptorClass.toString()).toString()));
                                }
                                if(interceptorPriority instanceof String){
                                    pi.setPriority((Double)JavaConversion.strToBasic(get(interceptorPriority.toString()).toString(),double.class));
                                }
                                if(interceptorPath instanceof String[]){
                                    pi.setPath((String[])interceptorPath);
                                }else if(interceptorPath instanceof String){
                                    pi.setPath(get(interceptorPath.toString()).toString());
                                }
                                if(interceptorExcludePath instanceof String[]){
                                    pi.setExcludePath((String) interceptorExcludePath);
                                }else if(interceptorExcludePath instanceof String){
                                    pi.setExcludePath(get(interceptorExcludePath.toString()).toString());
                                }
                                InterceptorRegistry.addHandlerInterceptor(pi);
                            }
                        }
                        if(webMap.containsKey("static-handler")){
                            Map<String, String> staticHandlerMap = (Map<String, String>) webMap.get("static-handler");
                            for(Map.Entry<String,String> entry:staticHandlerMap.entrySet()){
                                web.addStaticHander(entry.getKey(),get(entry.getKey()).toString());
                            }
                        }
                        if(webMap.containsKey("specifi-resources-restrict-ip")){
                            Map<String, Set<String>> srriMap = (Map<String, Set<String>>) webMap.get("specifi-resources-restrict-ip");
                            for(Map.Entry<String,Set<String>> entry:srriMap.entrySet()){
                                Set<String> valueSet = entry.getValue().stream().map($v -> get($v).toString()).collect(Collectors.toSet());
                                web.addSpecifiResourcesIpRestrict(entry.getKey(), valueSet);
                            }
                        }
                        if(webMap.containsKey("global-resources-restrict-ip")){
                            List<String> grriList = (List<String>) webMap.get("global-resources-restrict-ip");
                            for (String $value : grriList) {
                                web.addGlobalResourcesIpRestrict(get($value).toString());
                            }
                        }
                        if(webMap.containsKey("static-resources-restrict-ip")){
                            List<String> srriList= (List<String>) webMap.get("static-resources-restrict-ip");
                            for (String $value : srriList) {
                                web.addStaticResourcesIpRestrict(get($value).toString());
                            }
                        }
                        if(webMap.containsKey("error-page")){
                            Map<String, String> errorPageMap = (Map<String, String>) webMap.get("error-page");
                            for(Map.Entry<String,String> entry:errorPageMap.entrySet()){
                                web.addErrorPage(entry.getKey(),get(entry.getValue()).toString());
                            }
                        }
                        if(webMap.containsKey("favicon-ico")){
                            web.setFavicon(get("favicon-ico").toString());
                        }
                        if(webMap.containsKey("mapping-preprocess")){
                            web.setMappingPreprocess((MappingPreprocess) ClassUtils.newObject(get("mapping-preprocess").toString()));
                        }
                        if(webMap.containsKey("parameter-analysis-chain-add")){
                            List<String> parameterAnaList=(List<String>)webMap.get("parameter-analysis-chain-add");
                            for (String $value : parameterAnaList) {
                                web.addParameterAnalysis((ParameterAnalysis)ClassUtils.newObject(get($value).toString()));
                            }
                        }
                        if(webMap.containsKey("call-api")){
                            Map<String,String> callApi=(Map<String,String>)webMap.get("call-api");
                            web.setCallApi(callApi);
                        }
                        if(webMap.containsKey("response")){
                            web.setResponse((LuckyResponse) ClassUtils.newObject(get("response").toString()));
                        }
                        if(webMap.containsKey("parameter-enhance-chain-add")){
                            List<String> parameterEnhanceList=(List<String>)webMap.get("parameter-enhance-chain-add");
                            for (String $value : parameterEnhanceList) {
                                web.addParameterEnhance((ParameterEnhance) ClassUtils.newObject(get($value).toString()));
                            }
                        }
                        if(webMap.containsKey("serialization")){
                            Object serializationNode = webMap.get("serialization");
                            if(serializationNode instanceof Map){
                                Map<String,Object> serializationMap= (Map<String, Object>) serializationNode;
                                Object json = serializationMap.get("json");
                                Object xml = serializationMap.get("xml");
                                if(json instanceof String){
                                    web.setJsonSerializationScheme((JSONSerializationScheme)ClassUtils.newObject(get(json.toString()).toString()));
                                }
                                if(xml instanceof String){
                                    web.setXmlSerializationScheme((XMLSerializationScheme) ClassUtils.newObject(get(xml.toString()).toString()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
