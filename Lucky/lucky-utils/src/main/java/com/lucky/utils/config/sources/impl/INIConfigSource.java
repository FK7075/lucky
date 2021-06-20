package com.lucky.utils.config.sources.impl;

import com.lucky.utils.file.Resources;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/14 上午8:47
 */
public class INIConfigSource extends AbstractLocalFileConfigSource {

    public INIConfigSource(InputStream configInputStream) {
        super(configInputStream);
    }

    public INIConfigSource(String classPathPropertiesFile){
        this(Resources.getInputStream(classPathPropertiesFile));
    }

    @Override
    protected void initConfig(){
        try {
            INIFilePars pars = new INIFilePars(configInputStream);
            Map<String, Map<String, String>> iniMap = pars.getIniMap();
            configMap = new LinkedHashMap<>();
            iniMap.forEach((sectionName,sectionMap)->{
                String prefix = "@:"+sectionName+".";
                sectionMap.forEach((k,v)->{
                    configMap.put(prefix+k,v);
                });
            });
        }catch (Exception e){
            throw new ConfigSourceParsingException(e,"解析.ini文件时出现异常！");
        }
    }
}
