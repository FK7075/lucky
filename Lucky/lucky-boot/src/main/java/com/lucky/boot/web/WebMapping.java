package com.lucky.boot.web;

import javax.servlet.annotation.WebInitParam;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/7 0007 9:36
 */
public abstract class WebMapping {

    protected String name="";
    protected String[] urlPatterns={};
    protected Map<String,String> initParams;
    protected boolean asyncSupported=false;
    protected String smallIcon="";
    protected String largeIcon="";
    protected String description="";
    protected String displayName="";

    public WebMapping(){
        initParams=new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String[] urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public Map<String, String> getInitParams() {
        return initParams;
    }

    public void setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
    }

    public void addInitParam(String name,String value){
        this.initParams.put(name,value);
    }

    public void addInitParam(WebInitParam webInitParam){
        this.initParams.put(webInitParam.name(),webInitParam.value());
    }

    public void setInitParams(WebInitParam[] webInitParam){
        for (WebInitParam initParam : webInitParam) {
            addInitParam(initParam);
        }
    }

    public boolean isAsyncSupported() {
        return asyncSupported;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
