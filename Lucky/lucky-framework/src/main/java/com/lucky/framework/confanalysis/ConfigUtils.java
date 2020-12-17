package com.lucky.framework.confanalysis;

import com.lucky.utils.base.Assert;
import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.file.Resources;

import java.io.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午6:12
 */
public abstract class ConfigUtils {

    public static final String LUCKY_CONFIG_LOCATION="lucky.config.location";
    public static final String DEFAULT_CONFIG_A="/application.yaml";
    public static final String DEFAULT_CONFIG="/application.yml";
    private static YamlConfAnalysis yaml;

    public static YamlConfAnalysis getYamlConfAnalysis(){
        if(yaml==null){
            Reader confReader=null;
            String runYamlPath = System.getProperty(LUCKY_CONFIG_LOCATION);
            if(Assert.isNotNull(runYamlPath)){
                try {
                    confReader=new BufferedReader(new InputStreamReader(new FileInputStream(runYamlPath),"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }else if(Assert.isNotNull(Resources.getInputStream(DEFAULT_CONFIG_A))){
                confReader= Resources.getReader(DEFAULT_CONFIG_A);
            }else if(Assert.isNotNull(Resources.getInputStream(DEFAULT_CONFIG))){
                confReader=Resources.getReader(DEFAULT_CONFIG);
            }
            if(confReader==null){
                return null;
            }
            yaml=new YamlConfAnalysis(confReader);
        }
        return yaml;
    }
}
