package com.lucky.boot.conf;

import com.lucky.boot.web.FilterMapping;
import com.lucky.boot.web.ListenerMapping;
import com.lucky.boot.web.ServletMapping;
import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.reflect.ClassUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 10:58
 */
public abstract class YamlParsing {

    private static final YamlConfAnalysis yaml = ConfigUtils.getYamlConfAnalysis();

    public static void loadServer(ServerConfig conf){
        if(Assert.isNotNull(yaml)){
            load(yaml.getMap(),conf);
        }
        conf.setFirst(false);
    }

    private static Object get(String suffix){
        return yaml.getObject(suffix);
    }

    private static void load(Map<String,Object> config,ServerConfig server){
        if(config.containsKey("server")){
            Object serverNode = config.get("server");
            if(serverNode instanceof Map){
                Map<String,Object> serverMap= (Map<String, Object>) serverNode;
                if(serverMap.containsKey("port")){
                    Object port = serverMap.get("port");
                    if(port instanceof Integer){
                        server.setPort((int)port);
                    }else{
                        server.setPort((Integer) JavaConversion.strToBasic(get(port.toString()).toString(),int.class));
                    }
                }
                if(serverMap.containsKey("context-path")){
                    String contextPath = (String) serverMap.get("context-path");
                    server.setContextPath(get(contextPath).toString());
                }
                if(serverMap.containsKey("session-timeout")){
                    Object st = serverMap.get("session-timeout");
                    if(st instanceof Integer){
                        server.setSessionTimeout((Integer) st);
                    }else{
                        server.setSessionTimeout((Integer) JavaConversion.strToBasic(get(st.toString()).toString(),
                                int.class,true));
                    }
                }
                if(serverMap.containsKey("doc-base")){
                    String docBase = (String) serverMap.get("doc-base");
                    server.setDocBase(get(docBase).toString());
                }
                if(serverMap.containsKey("base-dir")){
                    String baseDir = (String) serverMap.get("base-dir");
                    server.setBaseDir(get(baseDir).toString());
                }
                if(serverMap.containsKey("webapp")){
                    String webapp = (String) serverMap.get("webapp");
                    server.setWebapp(get(webapp).toString());
                }
                if(serverMap.containsKey("auto-deploy")){
                    Object ad = serverMap.get("auto-deploy");
                    if(ad instanceof Boolean){
                        server.setAutoDeploy((Boolean)ad);
                    }else{
                        server.setAutoDeploy((Boolean)JavaConversion.strToBasic(get(ad.toString()).toString(),boolean.class));
                    }
                }
                if(serverMap.containsKey("reloadable")){
                    Object reloadable = serverMap.get("reloadable");
                    if(reloadable instanceof Boolean){
                        server.setReloadable((Boolean) reloadable);
                    }else{
                        server.setReloadable((Boolean)JavaConversion.strToBasic(get(reloadable.toString()).toString(),boolean.class));
                    }
                }
                if(serverMap.containsKey("close-port")){
                    Object closePort = serverMap.get("close-port");
                    if(closePort instanceof Integer) {
                        server.setClosePort((Integer) closePort);
                    }else{
                        server.setClosePort((Integer) JavaConversion.strToBasic(get(closePort.toString()).toString(),int.class));
                    }
                }
                if(serverMap.containsKey("shutdown")){
                    String shutdown = (String) serverMap.get("shutdown");
                    server.setShutdown(get(shutdown).toString());
                }
                if(serverMap.containsKey("url-encoding")){
                    String urlEncoding = (String) serverMap.get("url-encoding");
                    server.setURIEncoding(get(urlEncoding).toString());
                }
                if(serverMap.containsKey("listeners")){
                    Object obj = serverMap.get("listeners");
                    if(obj instanceof Map){
                        Map<String,String> listenerMap= (Map<String, String>) obj;
                        for(Map.Entry<String,String> entry:listenerMap.entrySet()){
                            ListenerMapping listenerMapping
                                    =new ListenerMapping(entry.getKey(),(EventListener)ClassUtils.newObject(get(entry.getValue()).toString()));
                            server.addListener(listenerMapping);
                        }
                    }
                }
                if(serverMap.containsKey("servlets")){
                    Object obj = serverMap.get("servlets");
                    if(obj instanceof List){
                        List<Map<String,Object>> servlets= (List<Map<String, Object>>) obj;
                        for (Map<String, Object> servlet : servlets) {
                            addServlet(server,servlet);
                        }
                    }else if(obj instanceof Map){
                        Map<String,Object> servlet= (Map<String, Object>) obj;
                        addServlet(server,servlet);
                    }
                }
                if(serverMap.containsKey("filters")){
                    Object obj = serverMap.get("filters");
                    if(obj instanceof List){
                        List<Map<String,Object>> filters= (List<Map<String, Object>>) obj;;
                        for (Map<String, Object> filter : filters) {
                            addFilter(server,filter);
                        }
                    }else if(obj instanceof Map){
                        Map<String,Object> filter= (Map<String, Object>) obj;
                        addFilter(server,filter);
                    }
                }
            }
        }
    }

    private static void addFilter(ServerConfig server,Map<String,Object> filterMap){
        Object filterName = filterMap.get("filter-name");
        Object filterClass = filterMap.get("filter-class");
        Object urlPatterns = filterMap.get("url-patterns");
        Object servletNames = filterMap.get("servlet-names");
        Object asyncSupported = filterMap.get("async-supported");
        Object dispatcherTypes = filterMap.get("dispatcher-types");
        Object initParams = filterMap.get("init-params");
        FilterMapping fm=new FilterMapping();
        if(filterName instanceof String){
            fm.setName(get(filterName.toString()).toString());
        }
        if(filterClass instanceof String){
            fm.setFilter((Filter)ClassUtils.newObject(get(filterClass.toString()).toString()));
        }
        if(urlPatterns instanceof List){
            List<String> $list= (List<String>) urlPatterns;
            fm.setUrlPatterns(ArrayUtils.listToArray(to$List($list)));
        }else if(urlPatterns instanceof String){
            String[] urls=new String[1];
            urls[0]= (String) get(urlPatterns.toString());
            fm.setUrlPatterns(urls);
        }
        if(servletNames instanceof List){
            List<String> $list= (List<String>) servletNames;
            fm.setServletNames(ArrayUtils.listToArray(to$List($list)));
        }else if(servletNames instanceof String){
            String[] urls=new String[1];
            urls[0]= (String) get(servletNames.toString());
            fm.setServletNames(urls);
        }
        if(asyncSupported instanceof Boolean){
            fm.setAsyncSupported((Boolean) asyncSupported);
        }else{
            fm.setAsyncSupported((Boolean)JavaConversion.strToBasic(get(asyncSupported.toString()).toString(),boolean.class));
        }
        if(dispatcherTypes instanceof List){
            List<String> $list= (List<String>) dispatcherTypes;
            String[] dispatcherArray = ArrayUtils.listToArray(to$List($list));
            fm.setDispatcherTypes(getDispatcherType(dispatcherArray));
        }else if(dispatcherTypes instanceof String){
            String[] urls=new String[1];
            urls[0]= (String) get(dispatcherTypes.toString());
            fm.setDispatcherTypes(getDispatcherType(urls));
        }
        if(initParams instanceof Map){
            Map<String,String> initParamsMap= (Map<String, String>) initParams;
            for(Map.Entry<String,String> entry:initParamsMap.entrySet()){
                fm.addInitParam(entry.getKey(),get(entry.getValue()).toString());
            }
        }
        server.addFilter(fm);

    }

    private static void addServlet(ServerConfig server,Map<String,Object> servletMap){
        Object servletName = servletMap.get("servlet-name");
        Object servletClass = servletMap.get("servlet-class");
        Object urlPatterns = servletMap.get("url-patterns");
        Object loadOnStartup = servletMap.get("load-on-startup");
        Object asyncSupported = servletMap.get("async-supported");
        Object initParams = servletMap.get("init-params");
        ServletMapping sm=new ServletMapping();
        if(servletName instanceof String){
            sm.setName(get(servletName.toString()).toString());
        }
        if(servletClass instanceof String){
            sm.setServlet((HttpServlet) ClassUtils.newObject(get(servletClass.toString()).toString()));
        }
        if(urlPatterns instanceof List){
           List<String> $list= (List<String>) urlPatterns;
           sm.setUrlPatterns(ArrayUtils.listToArray(to$List($list)));
        }else if(urlPatterns instanceof String){
            String[] urls=new String[1];
            urls[0]= (String) get(urlPatterns.toString());
            sm.setUrlPatterns(urls);
        }
        if(loadOnStartup instanceof Integer){
            sm.setLoadOnStartup((Integer) loadOnStartup);
        }else{
            sm.setLoadOnStartup((Integer)JavaConversion.strToBasic(get(loadOnStartup.toString()).toString(),int.class));
        }
        if(asyncSupported instanceof Boolean){
            sm.setAsyncSupported((Boolean) asyncSupported);
        }else{
            sm.setAsyncSupported((Boolean)JavaConversion.strToBasic(get(asyncSupported.toString()).toString(),boolean.class));
        }
        if(initParams instanceof Map){
            Map<String,String> initParamsMap= (Map<String, String>) initParams;
            for(Map.Entry<String,String> entry:initParamsMap.entrySet()){
                sm.addInitParam(entry.getKey(),get(entry.getValue()).toString());
            }
        }
        server.addServlet(sm);
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

    private static List<String> to$List(List<String> list){
        return list.stream().map($v->get($v).toString()).collect(Collectors.toList());
    }
}
