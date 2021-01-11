package com.lucky.web.servlet;

import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.utils.config.ConfigUtils;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.reflect.ClassUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/11 0011 10:11
 */
public class LuckyServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if(!AutoScanApplicationContext.isInit){
            Class<?> rootClass=null;
            YamlConfAnalysis yaml = ConfigUtils.getYamlConfAnalysis();
            if(yaml!=null){
                Map<String, Object> yamlMap = yaml.getMap();
                Object luckyNode = yamlMap.get("lucky");
                if(luckyNode instanceof Map){
                    Map<String,Object> luckyMap= (Map<String, Object>) luckyNode;
                    Object webNode = luckyMap.get("web");
                    if(webNode instanceof Map){
                        Map<String,Object> webMap= (Map<String, Object>) webNode;
                        Object rootClassNode = webMap.get("root-class");
                        if(rootClassNode!=null){
                            rootClass= ClassUtils.getClass(rootClassNode.toString());
                        }
                    }
                }
            }
            AutoScanApplicationContext.create(rootClass);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
