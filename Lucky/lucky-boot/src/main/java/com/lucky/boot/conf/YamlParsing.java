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

    private static Object get(Object suffix){
        return yaml.getObject(suffix);
    }

    private static void load(Map<String,Object> config,ServerConfig server){
        if(config.containsKey("server")){
            Object serverNode = config.get("server");
            if(serverNode instanceof Map){
                Map<String,Object> serverMap= (Map<String, Object>) serverNode;
                if(serverMap.containsKey("port")){
                    Object port = get(serverMap.get("port"));
                    if(port instanceof Integer){
                        server.setPort((int)port);
                    }else{
                        server.setPort((Integer) JavaConversion.strToBasic(port.toString(),int.class));
                    }
                }
                if(serverMap.containsKey("context-path")){
                    server.setContextPath(get(serverMap.get("context-path")).toString());
                }
                if(serverMap.containsKey("session-timeout")){
                    Object st = get(serverMap.get("session-timeout"));
                    if(st instanceof Integer){
                        server.setSessionTimeout((Integer) st);
                    }else{
                        server.setSessionTimeout((Integer) JavaConversion.strToBasic(st.toString(),
                                int.class,true));
                    }
                }
                if(serverMap.containsKey("doc-base")){
                    server.setDocBase(get(serverMap.get("doc-base")).toString());
                }
                if(serverMap.containsKey("base-dir")){
                    server.setBaseDir(get(serverMap.get("base-dir")).toString());
                }
                if(serverMap.containsKey("webapp")){
                    server.setWebapp(get(serverMap.get("webapp")).toString());
                }
                if(serverMap.containsKey("auto-deploy")){
                    Object ad = get(serverMap.get("auto-deploy"));
                    if(ad instanceof Boolean){
                        server.setAutoDeploy((Boolean)ad);
                    }else{
                        server.setAutoDeploy((Boolean)JavaConversion.strToBasic(ad.toString(),boolean.class));
                    }
                }
                if(serverMap.containsKey("reloadable")){
                    Object reloadable = get(serverMap.get("reloadable"));
                    if(reloadable instanceof Boolean){
                        server.setReloadable((Boolean) reloadable);
                    }else{
                        server.setReloadable((Boolean)JavaConversion.strToBasic(reloadable.toString(),boolean.class));
                    }
                }
                if(serverMap.containsKey("close-port")){
                    Object closePort = get(serverMap.get("close-port"));
                    if(closePort instanceof Integer) {
                        server.setClosePort((Integer) closePort);
                    }else{
                        server.setClosePort((Integer) JavaConversion.strToBasic(closePort.toString(),int.class));
                    }
                }
                if(serverMap.containsKey("shutdown")){
                    server.setShutdown(get(serverMap.get("shutdown")).toString());
                }
                if(serverMap.containsKey("url-encoding")){
                    server.setURIEncoding(get(serverMap.get("url-encoding")).toString());
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
        Object filterName = get(filterMap.get("filter-name"));
        Object filterClass = get(filterMap.get("filter-class"));
        Object urlPatterns = get(filterMap.get("url-patterns"));
        Object servletNames = get(filterMap.get("servlet-names"));
        Object asyncSupported = get(filterMap.get("async-supported"));
        Object dispatcherTypes = get(filterMap.get("dispatcher-types"));
        Object initParams = get(filterMap.get("init-params"));
        FilterMapping fm=new FilterMapping();
        if(filterName instanceof String){
            fm.setName(filterName.toString());
        }
        if(filterClass instanceof String){
            fm.setFilter((Filter)ClassUtils.newObject(filterClass.toString()));
        }
        if(urlPatterns instanceof List){
            List<String> $list= (List<String>) urlPatterns;
            fm.setUrlPatterns(listToArrayByStr(to$List($list)));
        }else if(urlPatterns instanceof String){
            String[] urls=new String[1];
            urls[0]= (String) urlPatterns;
            fm.setUrlPatterns(urls);
        }
        if(servletNames instanceof List){
            List<String> $list= (List<String>) servletNames;
            fm.setServletNames(listToArrayByStr(to$List($list)));
        }else if(servletNames instanceof String){
            String[] urls=new String[1];
            urls[0]= (String) servletNames;
            fm.setServletNames(urls);
        }
        if(asyncSupported instanceof Boolean){
            fm.setAsyncSupported((Boolean) asyncSupported);
        }else{
            fm.setAsyncSupported((Boolean)JavaConversion.strToBasic(asyncSupported.toString(),boolean.class));
        }
        if(dispatcherTypes instanceof List){
            List<String> $list= (List<String>) dispatcherTypes;
            String[] dispatcherArray = listToArrayByStr(to$List($list));
            fm.setDispatcherTypes(getDispatcherType(dispatcherArray));
        }else if(dispatcherTypes instanceof String){
            String[] urls=new String[1];
            urls[0]= (String)dispatcherTypes;
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
        Object servletName = get(servletMap.get("servlet-name"));
        Object servletClass = get(servletMap.get("servlet-class"));
        Object urlPatterns = get(servletMap.get("url-patterns"));
        Object loadOnStartup = get(servletMap.get("load-on-startup"));
        Object asyncSupported = get(servletMap.get("async-supported"));
        Object initParams = get(servletMap.get("init-params"));
        ServletMapping sm=new ServletMapping();
        if(servletName instanceof String){
            sm.setName(servletName.toString());
        }
        if(servletClass instanceof String){
            sm.setServlet((HttpServlet) ClassUtils.newObject(servletName.toString()));
        }
        if(urlPatterns instanceof List){
           List<String> $list= (List<String>) urlPatterns;
           sm.setUrlPatterns(listToArrayByStr(to$List($list)));
        }else if(urlPatterns instanceof String){
            String[] urls=new String[1];
            urls[0]= (String) urlPatterns;
            sm.setUrlPatterns(urls);
        }
        if(loadOnStartup instanceof Integer){
            sm.setLoadOnStartup((Integer) loadOnStartup);
        }else{
            sm.setLoadOnStartup((Integer)JavaConversion.strToBasic(loadOnStartup.toString(),int.class));
        }
        if(asyncSupported instanceof Boolean){
            sm.setAsyncSupported((Boolean) asyncSupported);
        }else{
            sm.setAsyncSupported((Boolean)JavaConversion.strToBasic(asyncSupported.toString(),boolean.class));
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

    private static String[] listToArrayByStr(List<String> list){
        String[] array=new String[list.size()];
        list.toArray(array);
        return array;
    }
}
