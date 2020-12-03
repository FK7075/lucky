package com.lucky.jacklamb.boot.conf;

import com.lucky.framework.confanalysis.ConfigUtils;
import com.lucky.framework.confanalysis.YamlConfAnalysis;
import com.lucky.framework.serializable.JSONSerializationScheme;
import com.lucky.framework.serializable.XMLSerializationScheme;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.conversion.JavaConversion;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.core.LuckyResponse;
import com.lucky.web.core.MappingPreprocess;
import com.lucky.web.core.ParameterProcess;
import com.lucky.web.core.parameter.ParameterAnalysis;

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
            }
        }
    }
}
