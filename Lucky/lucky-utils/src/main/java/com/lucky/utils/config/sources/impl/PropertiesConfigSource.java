package com.lucky.utils.config.sources.impl;

import com.lucky.utils.file.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/14 上午8:33
 */
public class PropertiesConfigSource extends AbstractLocalFileConfigSource {


    public PropertiesConfigSource(InputStream configInputStream) {
        super(configInputStream);
    }

    public PropertiesConfigSource(String classPathPropertiesFile){
        this(Resources.getInputStream(classPathPropertiesFile));
    }

    @Override
    protected void initConfig(){
        try {
            Properties properties = new Properties();
            properties.load(configInputStream);
            configMap= new LinkedHashMap<>(properties.size());
            properties.forEach((k,v)->configMap.put(k.toString(),v));
        } catch (IOException e) {
            throw new ConfigSourceParsingException(e,"解析.properties文件时出现异常！");
        }
    }
}
