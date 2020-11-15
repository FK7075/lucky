package com.lucky.jacklamb.framework.confanalysis;

import com.lucky.jacklamb.framework.uitls.base.Assert;
import com.lucky.jacklamb.framework.uitls.file.Resources;

import java.io.BufferedReader;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午6:12
 */
public abstract class ConfigUtils {

    private static final String[] YAML={".yaml",".yml"};

    public static ConfAnalysis getConfAnalysis(String resourceFilePath){
        resourceFilePath=resourceFilePath.startsWith("/")?resourceFilePath:"/"+resourceFilePath;
        BufferedReader reader = Resources.getReader(resourceFilePath);
        if(Assert.isNull(reader)){
            throw new RuntimeException();
        }
        if(Assert.strEndsWith(resourceFilePath,YAML)){
            return new YamlConfAnalysis(reader);
        }
        if(resourceFilePath.endsWith(".properties")){
            return new PropertyConfAnalysis(reader);
        }
        if(resourceFilePath.endsWith(".ini")){
            return new IniConfAnalysis(reader);
        }
        if(resourceFilePath.endsWith(".xml")){
            return new XmlConfAnalysis(reader);
        }
        throw new RuntimeException();
    }
}
