package com.lucky.thymeleaf.conf;

import com.lucky.framework.confanalysis.LuckyConfig;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/1 3:04 下午
 */
public class ThymeleafConfig extends LuckyConfig {

    private static ThymeleafConfig conf;

    private boolean enabled;
    private String encoding;
    private String prefix;
    private String suffix;
    private boolean cache;
    private String model;

    private ThymeleafConfig(){

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrefix() {
        if(prefix.startsWith("classpath:")){
            prefix=prefix.substring(10);
        }
        return prefix;
    }

    public static ThymeleafConfig defaultThymeleafConfig() {
        if(conf==null){
            conf=new ThymeleafConfig();
            conf.setCache(false);
            conf.setEnabled(false);
            conf.setEncoding("UTF-8");
            conf.setModel("HTML");
            conf.setPrefix("classpath:/templates/");
            conf.setSuffix(".html");
            conf.setFirst(true);
        }
        return conf;
    }

    public static ThymeleafConfig getThymeleafConfig(){
        ThymeleafConfig conf = defaultThymeleafConfig();
        if(conf.isFirst()){
            YamlParsing.loadThymeleaf(conf);
        }
        return conf;
    }
}
