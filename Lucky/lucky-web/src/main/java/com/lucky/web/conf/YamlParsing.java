package com.lucky.web.conf;

import com.lucky.framework.serializable.JSONSerializationScheme;
import com.lucky.framework.serializable.XMLSerializationScheme;
import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;
import com.lucky.utils.base.BaseUtils;
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

    public static void loadWeb(WebConfig conf){
        yaml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static Object get(String suffix){
        return yaml.getObject(suffix);
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
                            String encoding = (String) webMap.get("encoding");
                            web.setEncoding((String)get(encoding));
                        }
                        if(webMap.containsKey("web-root")){
                            String webRoot = (String) webMap.get("web-root");
                            web.setWebRoot((String)get(webRoot));
                        }
                        if(webMap.containsKey("post-change-method")){
                            Object pcm = webMap.get("post-change-method");
                            if(pcm instanceof Boolean){
                                web.setPostChangeMethod((Boolean) pcm);
                            }else{
                                web.setPostChangeMethod((Boolean)JavaConversion.strToBasic(get(pcm.toString()).toString(),boolean.class));
                            }
                        }
                        if(webMap.containsKey("static-resource-manage")){
                            Object srm = webMap.get("static-resource-manage");
                            if(srm instanceof Boolean){
                                web.setOpenStaticResourceManage((boolean)srm);
                            }else{
                                web.setOpenStaticResourceManage((Boolean)JavaConversion.strToBasic(get(srm.toString()).toString(),boolean.class));
                            }

                        }
                        if(webMap.containsKey("multipart-max-file-size")){
                            Object mmfs = webMap.get("multipart-max-file-size");
                            if(mmfs instanceof Integer){
                                web.setMultipartMaxFileSize((int)mmfs);
                            }else{
                                web.setMultipartMaxFileSize((int) JavaConversion.strToBasic(get(mmfs.toString()).toString(), int.class, true));
                            }
                        }
                        if(webMap.containsKey("multipart-max-request-size")){
                            Object mmrs = webMap.get("multipart-max-request-size");
                            if(mmrs instanceof Integer){
                                web.setMultipartMaxRequestSize((int)mmrs);
                            }else{
                                web.setMultipartMaxRequestSize((int) JavaConversion.strToBasic(
                                        get(mmrs.toString()).toString(),
                                        int.class,
                                        true));
                            }
                        }
                        if(webMap.containsKey("prefix")){
                            String prefix = (String) webMap.get("prefix");
                            web.setPrefix((String)get(prefix));
                        }
                        if(webMap.containsKey("suffix")){
                            String suffix = (String) webMap.get("suffix");
                            web.setSuffix((String)get(suffix));
                        }
                        if(webMap.containsKey("httpclient-connection-timeout")){
                            Object hct = webMap.get("httpclient-connection-timeout");
                            if(hct instanceof Integer){
                                web.setConnectTimeout((int)hct);
                            }else{
                                web.setConnectTimeout((int)JavaConversion.strToBasic(
                                        get(hct.toString()).toString(),
                                        int.class,
                                        true));
                            }

                        }
                        if(webMap.containsKey("httpclient-request-timeout")){
                            Object hrt = webMap.get("httpclient-request-timeout");
                            if(hrt instanceof Integer){
                                web.setRequestTimeout((Integer) hrt);
                            }else{
                                web.setRequestTimeout((int)JavaConversion.strToBasic(
                                        get(hrt.toString()).toString(),
                                        int.class,
                                        true));
                            }
                        }
                        if(webMap.containsKey("httpclient-socket-timeout")){
                            Object hst = webMap.get("httpclient-socket-timeout");
                            if(hst instanceof Integer){
                                web.setSocketTimeout((int) hst);
                            }else{
                                web.setSocketTimeout((int)JavaConversion.strToBasic(
                                        get(hst.toString()).toString(),
                                        int.class,
                                        true));
                            }

                        }
                        if(webMap.containsKey("interceptors")){
                            Object interceptorsObj = webMap.get("interceptors");
                            if(interceptorsObj instanceof List){
                                List<Map<String,Object>> interceptors= (List<Map<String, Object>>)interceptorsObj;
                                for (Map<String, Object> interceptor : interceptors) {
                                    addInterceptor(web,interceptor);
                                }
                            }else if(interceptorsObj instanceof Map){
                                Map<String,Object> interceptor= (Map<String, Object>) interceptorsObj;
                                addInterceptor(web,interceptor);
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
                            String ico = (String) webMap.get("favicon-ico");
                            web.setFavicon(get(ico).toString());
                        }
                        if(webMap.containsKey("mapping-preprocess")){
                            String mp = (String) webMap.get("mapping-preprocess");
                            web.setMappingPreprocess((MappingPreprocess) ClassUtils.newObject(get(mp).toString()));
                        }
                        if(webMap.containsKey("parameter-analysis-chain-add")){
                            List<String> parameterAnaList=(List<String>)webMap.get("parameter-analysis-chain-add");
                            for (String $value : parameterAnaList) {
                                web.addParameterAnalysis((ParameterAnalysis)ClassUtils.newObject(get($value).toString()));
                            }
                        }
                        if(webMap.containsKey("call-api")){
                            Map<String,String> callApi=(Map<String,String>)webMap.get("call-api");
                            for(Map.Entry<String,String> entry:callApi.entrySet()){
                                web.addCallApi(entry.getKey(),get(entry.getValue()).toString());
                            }
                        }
                        if(webMap.containsKey("response")){
                            String response = (String) webMap.get("response");
                            web.setResponse((LuckyResponse) ClassUtils.newObject(get(response).toString()));
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

    private static void addInterceptor(WebConfig web,Map<String,Object> interceptorMap){
        PathAndInterceptor pi=new PathAndInterceptor();
        Object interceptorClass = interceptorMap.get("interceptor-class");
        Object interceptorPriority = interceptorMap.get("priority");
        Object interceptorPath = interceptorMap.get("path");
        Object interceptorExcludePath = interceptorMap.get("exclude-path");
        if(interceptorClass instanceof String){
            pi.setInterceptor((HandlerInterceptor)ClassUtils.newObject(get(interceptorClass.toString()).toString()));
        }
        if(interceptorPriority instanceof String){
            pi.setPriority((Double)JavaConversion.strToBasic(get(interceptorPriority.toString()).toString(),double.class));
        }
        if(interceptorPath instanceof List){
            List<String> list= (List<String>) interceptorPath;
            pi.setPath(ArrayUtils.listToArray(to$List(list)));
        }else if(interceptorPath instanceof String){
            pi.setPath(get(interceptorPath.toString()).toString());
        }
        if(interceptorExcludePath instanceof List){
            List<String> list= (List<String>) interceptorExcludePath;
            pi.setExcludePath(ArrayUtils.listToArray(to$List(list)));
        }else if(interceptorExcludePath instanceof String){
            pi.setExcludePath(get(interceptorExcludePath.toString()).toString());
        }
        InterceptorRegistry.addHandlerInterceptor(pi);
    }

    private static List<String> to$List(List<String> list){
        return list.stream().map($v->get($v).toString()).collect(Collectors.toList());
    }
}
