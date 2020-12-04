package com.lucky.boot.conf;

import com.lucky.framework.confanalysis.ConfigUtils;
import com.lucky.framework.confanalysis.YamlConfAnalysis;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.conversion.JavaConversion;
import com.lucky.framework.uitls.reflect.ClassUtils;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.util.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 10:58
 */
public abstract class YamlParsing {

    public static void loadServer(ServerConfig conf){
        YamlConfAnalysis yml = ConfigUtils.getYamlConfAnalysis();
        if(Assert.isNotNull(yml)){
            load(yml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static void load(Map<String,Object> config,ServerConfig server){
        if(config.containsKey("server")){
            Object serverNode = config.get("server");
            if(serverNode instanceof Map){
                Map<String,Object> serverMap= (Map<String, Object>) serverNode;
                if(serverMap.containsKey("port")){
                    server.setPort((Integer) JavaConversion.strToBasic(serverMap.get("port").toString(),int.class));
                }
                if(serverMap.containsKey("context-path")){
                    server.setContextPath(serverMap.get("context-path").toString());
                }
                if(serverMap.containsKey("session-timeout")){
                    server.setSessionTimeout((Integer) JavaConversion.strToBasic(serverMap.get("session-timeout").toString(),
                            int.class,true));
                }
                if(serverMap.containsKey("doc-base")){
                    server.setDocBase(serverMap.get("doc-base").toString());
                }
                if(serverMap.containsKey("base-dir")){
                    server.setBaseDir(serverMap.get("base-dir").toString());
                }
                if(serverMap.containsKey("webapp")){
                    server.setWebapp(serverMap.get("webapp").toString());
                }
                if(serverMap.containsKey("auto-deploy")){
                    server.setAutoDeploy((Boolean) serverMap.get("auto-deploy"));
                }
                if(serverMap.containsKey("reloadable")){
                    server.setReloadable((Boolean) serverMap.get("reloadable"));
                }
                if(serverMap.containsKey(" close-port")){
                    server.setClosePort((Integer) JavaConversion.strToBasic(serverMap.get(" close-port").toString(),int.class));
                }
                if(serverMap.containsKey("shutdown")){
                    server.setShutdown(serverMap.get("shutdown").toString());
                }
                if(serverMap.containsKey("url-encoding")){
                    server.setURIEncoding(serverMap.get("url-encoding").toString());
                }
                if(serverMap.containsKey("listener")){
                    Object obj = serverMap.get("listener");
                    if(obj instanceof Map){
                        Map<String,String> listenerMap= (Map<String, String>) obj;
                        Collection<String> values = listenerMap.values();
                        for (String value : values) {
                            server.addListener((EventListener) ClassUtils.newObject(value));
                        }
                    }
                }
                if(serverMap.containsKey("servlet")){
                    Object obj = serverMap.get("servlet");
                    if(obj instanceof Map){
                        Map<String,Map<String,Object>> servletMap= (Map<String, Map<String, Object>>) obj;
                        Collection<Map<String, Object>> values = servletMap.values();
                        for (Map<String, Object> value : values) {
                            Object mapping = value.get("mapping");
                            int loadOnStartUp=value.containsKey("load-on-startup")?(int)value.get("load-on-startup"):-1;
                            if(mapping instanceof String){
                                server.addServlet((HttpServlet)ClassUtils.newObject(value.get("class").toString()),loadOnStartUp,
                                        mapping.toString());
                            }else{
                                ArrayList<String> m= (ArrayList<String>) mapping;
                                String[] ma=new String[m.size()];
                                m.toArray(ma);
                                server.addServlet((HttpServlet)ClassUtils.newObject(value.get("class").toString()),loadOnStartUp,
                                        ma);
                            }
                        }
                    }
                }
                if(serverMap.containsKey("filter")){
                    Object obj = serverMap.get("filter");
                    if(obj instanceof Map){
                        Map<String,Map<String,Object>> filterMap= (Map<String, Map<String, Object>>) obj;
                        Collection<Map<String, Object>> values = filterMap.values();
                        for (Map<String, Object> value : values) {
                            Object mapping = value.get("mapping");
                            if(mapping instanceof String){
                                server.addFilter((Filter)ClassUtils.newObject(value.get("class").toString()),
                                       mapping.toString());
                            }else{
                                ArrayList<String> m= (ArrayList<String>) mapping;
                                String[] ma=new String[m.size()];
                                m.toArray(ma);
                                server.addFilter((Filter)ClassUtils.newObject(value.get("class").toString()),
                                        ma);
                            }

                        }
                    }
                }

            }
        }
    }
}
