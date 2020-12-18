package com.lucky.boot.conf;

import com.lucky.boot.web.FilterMapping;
import com.lucky.boot.web.ListenerMapping;
import com.lucky.boot.web.ServletMapping;
import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.ClassUtils;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Map;

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
                if(serverMap.containsKey("close-port")){
                    server.setClosePort((Integer) JavaConversion.strToBasic(serverMap.get("close-port").toString(),int.class));
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
                        Collection<String> keys = listenerMap.keySet();
                        for (String key : keys) {
                            ListenerMapping listenerMapping=new ListenerMapping(key,(EventListener) ClassUtils.newObject(listenerMap.get(key)));
                            server.addListener(listenerMapping);
                        }
                    }
                }
                if(serverMap.containsKey("servlet")){
                    Object obj = serverMap.get("servlet");
                    if(obj instanceof Map){
                        Map<String,Map<String,Object>> servletMap= (Map<String, Map<String, Object>>) obj;
                        for(Map.Entry<String,Map<String,Object>> servletEntry:servletMap.entrySet()){
                            ServletMapping sm=new ServletMapping();
                            sm.setName(servletEntry.getKey());
                            Map<String, Object> servletInfo = servletEntry.getValue();
                            if(servletInfo.containsKey("class")){
                                sm.setServlet((HttpServlet)ClassUtils.newObject(servletInfo.get("class").toString()));
                            }
                            if(servletInfo.containsKey("load-on-startup")){
                                sm.setLoadOnStartup((int)servletInfo.get("load-on-startup"));
                            }
                            if(servletInfo.containsKey("async-supported")){
                                sm.setAsyncSupported((boolean)servletInfo.get("async-supported"));
                            }
                            if(servletInfo.containsKey("init-params")){
                                sm.setInitParams((Map<String,String>)servletInfo.get("init-params"));
                            }
                            if(servletInfo.containsKey("url-patterns")){
                                Object url = servletInfo.get("url-patterns");
                                if(url instanceof String){
                                    String[] urlArray=new String[1];
                                    urlArray[0]=url.toString();
                                    sm.setUrlPatterns(urlArray);
                                }else{
                                    ArrayList<String> ls= (ArrayList<String>) url;
                                    String[] urlArray=new String[ls.size()];
                                    ls.toArray(urlArray);
                                    sm.setUrlPatterns(urlArray);
                                }
                            }
                            server.addServlet(sm);
                        }
                    }
                }
                if(serverMap.containsKey("filter")){
                    Object obj = serverMap.get("filter");
                    if(obj instanceof Map){
                        Map<String,Map<String,Object>> filterMap= (Map<String, Map<String, Object>>) obj;
                        for (Map.Entry<String,Map<String,Object>> filterEntry:filterMap.entrySet()){
                            FilterMapping fm=new FilterMapping();
                            fm.setName(filterEntry.getKey());
                            Map<String, Object> filterInfo = filterEntry.getValue();
                            if(filterInfo.containsKey("class")){
                                fm.setFilter((Filter)ClassUtils.newObject(filterInfo.get("class").toString()));
                            }
                            if(filterInfo.containsKey("async-supported")){
                                fm.setAsyncSupported((boolean)filterInfo.get("async-supported"));
                            }
                            if(filterInfo.containsKey("init-params")){
                                fm.setInitParams((Map<String,String>)filterInfo.get("init-params"));
                            }
                            if(filterInfo.containsKey("url-patterns")){
                                Object url = filterInfo.get("url-patterns");
                                if(url instanceof String){
                                    String[] urlArray=new String[1];
                                    urlArray[0]=url.toString();
                                    fm.setUrlPatterns(urlArray);
                                }else{
                                    ArrayList<String> ls= (ArrayList<String>) url;
                                    String[] urlArray=new String[ls.size()];
                                    ls.toArray(urlArray);
                                    fm.setUrlPatterns(urlArray);
                                }
                            }

                            if(filterInfo.containsKey("servlet-names")){
                                Object url = filterInfo.get("servlet-names");
                                if(url instanceof String){
                                    String[] urlArray=new String[1];
                                    urlArray[0]=url.toString();
                                    fm.setServletNames(urlArray);
                                }else{
                                    ArrayList<String> ls= (ArrayList<String>) url;
                                    String[] urlArray=new String[ls.size()];
                                    ls.toArray(urlArray);
                                    fm.setServletNames(urlArray);
                                }
                            }

                            if(filterInfo.containsKey("dispatcher-types")){
                                Object url = filterInfo.get("dispatcher-types");
                                if(url instanceof String){
                                    DispatcherType[] dispatcherTypes=new DispatcherType[1];
                                    dispatcherTypes[0]=getDispatcherType(url.toString());
                                    fm.setDispatcherTypes(dispatcherTypes);
                                }else{
                                    ArrayList<String> ls= (ArrayList<String>) url;
                                    String[] urlArray=new String[ls.size()];
                                    ls.toArray(urlArray);
                                    fm.setDispatcherTypes(getDispatcherType(urlArray));
                                }
                            }
                            server.addFilter(fm);
                        }
                    }

                }

            }
        }
    }

    private static DispatcherType[] getDispatcherType(String[] type){
        DispatcherType[] dtypes=new DispatcherType[type.length];
        for (int i = 0,j=type.length; i < j; i++) {
            dtypes[i]=getDispatcherType(type[i]);
        }
        return dtypes;
    }

    private static DispatcherType getDispatcherType(String type){
        switch (type.toUpperCase()){
            case "FORWARD":return DispatcherType.FORWARD;
            case "INCLUDE":return DispatcherType.INCLUDE;
            case "REQUEST":return DispatcherType.REQUEST;
            case "ASYNC":return DispatcherType.ASYNC;
            case "ERROR":return DispatcherType.ERROR;
        }
        throw new RuntimeException("错误的DispatcherType："+type);
    }
}
