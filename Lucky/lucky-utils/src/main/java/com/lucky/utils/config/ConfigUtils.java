package com.lucky.utils.config;

import com.lucky.utils.base.Assert;
import com.lucky.utils.file.*;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午6:12
 */
public abstract class ConfigUtils {

    public static final String LUCKY_CONFIG_LOCATION="lucky.conf.location";
    public static final String DEFAULT_CONFIG_YAML ="/application.yaml";
    public static final String DEFAULT_CONFIG_YML ="/application.yml";
    private static YamlConfAnalysis yaml;

    public static YamlConfAnalysis getYamlConfAnalysis(){
        if(yaml==null){
            Reader reader = getReader();
            if (reader==null){
                return null;
            }
            yaml=new YamlConfAnalysis(getReader());
        }
        return yaml;
    }

    public static void printYaml() throws IOException {
        Reader reader = getReader();
        if(reader!=null){
            StringWriter sw=new StringWriter();
            IOUtils.copy(reader,sw);
            System.out.println(sw.toString());
            return;
        }
        System.out.println("没有找到默认配置文件...");
    }

    private static Reader getReader(){
        String runYamlPath = System.getProperty(LUCKY_CONFIG_LOCATION);
        if(Assert.isNotNull(runYamlPath)){
            try {
                return new BufferedReader(new InputStreamReader(new FileInputStream(runYamlPath),"UTF-8"));
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if(Assert.isNotNull(Resources.getInputStream(DEFAULT_CONFIG_YAML))){
            return Resources.getReader(DEFAULT_CONFIG_YAML);
        }
        if(Assert.isNotNull(Resources.getInputStream(DEFAULT_CONFIG_YML))){
            return Resources.getReader(DEFAULT_CONFIG_YML);
        }
        return null;
    }
}
