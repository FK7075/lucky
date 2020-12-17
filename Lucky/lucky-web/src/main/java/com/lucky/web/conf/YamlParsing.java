package com.lucky.web.conf;

import com.lucky.framework.confanalysis.ConfigUtils;
import com.lucky.framework.serializable.JSONSerializationScheme;
import com.lucky.framework.serializable.XMLSerializationScheme;
import com.lucky.utils.base.Assert;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.web.core.LuckyResponse;
import com.lucky.web.core.MappingPreprocess;
import com.lucky.web.core.parameter.analysis.ParameterAnalysis;
import com.lucky.web.core.parameter.enhance.ParameterEnhance;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 10:58
 */
public abstract class YamlParsing {

    public static void loadWeb(WebConfig conf){
        YamlConfAnalysis yml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yml)){
            load(yml.getMap(),conf);
        }
        conf.setFirst(false);
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
                            web.setEncoding((String)webMap.get("encoding"));
                        }
                        if(webMap.containsKey("web-root")){
                            web.setWebRoot((String)webMap.get("web-root"));
                        }
                        if(webMap.containsKey("post-change-method")){
                            web.setPostChangeMethod((boolean)webMap.get("post-change-method"));
                        }
                        if(webMap.containsKey("static-resource-manage")){
                            web.setOpenStaticResourceManage((boolean)webMap.get("static-resource-manage"));
                        }
                        if(webMap.containsKey("multipart-max-file-size")){
                            web.setMultipartMaxFileSize((int) JavaConversion.strToBasic(
                                    webMap.get("multipart-max-file-size").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("multipart-max-request-size")){
                            web.setMultipartMaxRequestSize((int) JavaConversion.strToBasic(
                                    webMap.get("multipart-max-request-size").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("prefix")){
                            web.setPrefix((String)webMap.get("prefix"));
                        }
                        if(webMap.containsKey("suffix")){
                            web.setSuffix((String)webMap.get("suffix"));
                        }
                        if(webMap.containsKey("httpclient-connection-timeout")){
                            web.setConnectTimeout((int)JavaConversion.strToBasic(
                                    webMap.get("httpclient-connection-timeout").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("httpclient-request-timeout")){
                            web.setRequestTimeout((int)JavaConversion.strToBasic(
                                    webMap.get("httpclient-request-timeout").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("httpclient-socket-timeout")){
                            web.setSocketTimeout((int)JavaConversion.strToBasic(
                                   webMap.get("httpclient-socket-timeout").toString(),
                                    int.class,
                                    true));
                        }
                        if(webMap.containsKey("static-handler")){
                            web.setStaticHander((Map<String, String>) webMap.get("static-handler"));
                        }
                        if(webMap.containsKey("specifi-resources-restrict-ip")){
                            web.setSpecifiResourcesIpRestrict((Map<String, Set<String>>) webMap.get("specifi-resources-restrict-ip"));
                        }
                        if(webMap.containsKey("global-resources-restrict-ip")){
                            web.setGlobalResourcesIpRestrict(new HashSet<String>((List)webMap.get("global-resources-restrict-ip")));
                        }
                        if(webMap.containsKey("static-resources-restrict-ip")){
                            web.setStaticResourcesIpRestrict(new HashSet<>((List)webMap.get("static-resources-restrict-ip")));
                        }
                        if(webMap.containsKey("error-page")){
                            web.addAllErrorPage((Map<String, String>) webMap.get("error-page"));
                        }
                        if(webMap.containsKey("favicon-ico")){
                            web.setFavicon(webMap.get("favicon-ico").toString());
                        }
                        if(webMap.containsKey("mapping-preprocess")){
                            web.setMappingPreprocess((MappingPreprocess) ClassUtils.newObject(webMap.get("mapping-preprocess").toString()));
                            web.setFavicon(webMap.get("favicon-ico").toString());
                        }
                        if(webMap.containsKey("parameter-analysis-chain-add")){
                            List<String> paramana=(List<String>)webMap.get("parameter-analysis-chain-add");
                            paramana.stream().forEach(str->web.addParameterAnalysis((ParameterAnalysis) ClassUtils.newObject(str)));
                        }
                        if(webMap.containsKey("call-api")){
                            Map<String,String> callApi=(Map<String,String>)webMap.get("call-api");
                            web.setCallApi(callApi);
                        }
                        if(webMap.containsKey("response")){
                            web.setResponse((LuckyResponse) ClassUtils.newObject(webMap.get("response").toString()));
                        }
                        if(webMap.containsKey("parameter-enhance-chain-add")){
                            List<String> paramana=(List<String>)webMap.get("parameter-enhance-chain-add");
                            paramana.stream().forEach(str->web.addParameterEnhance((ParameterEnhance) ClassUtils.newObject(str)));
                        }
                        if(webMap.containsKey("serialization")){
                            Object serializationNode = webMap.get("serialization");
                            if(serializationNode instanceof Map){
                                Map<String,Object> serializationMap= (Map<String, Object>) serializationNode;
                                if(serializationMap.containsKey("json")){
                                    web.setJsonSerializationScheme((JSONSerializationScheme) ClassUtils.newObject(serializationMap.get("json").toString()));
                                }
                                if(serializationMap.containsKey("xml")){
                                    web.setXmlSerializationScheme((XMLSerializationScheme) ClassUtils.newObject(serializationMap.get("xml").toString()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
