package com.lucky.utils.config.sources.impl;
import com.lucky.utils.file.Resources;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/14 上午8:17
 */
public class YamlConfigSource extends AbstractLocalFileConfigSource {

    public YamlConfigSource(String classPathPropertiesFile){
        this(Resources.getInputStream(classPathPropertiesFile));
    }

    public YamlConfigSource(InputStream configInputStream) {
        super(configInputStream);
    }

    @Override
    protected void initConfig(){
        try {
            Yaml yaml=new Yaml();
            configMap = yaml.load(configInputStream);
        }catch (Exception e){
            throw new ConfigSourceParsingException(e,"解析.yaml/.yml文件时出现异常！");
        }

    }
}
