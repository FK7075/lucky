package com.lucky.utils.config.sources.impl;

import com.lucky.utils.config.sources.LocalFileConfigSource;

import java.io.InputStream;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/14 上午8:33
 */
public abstract class AbstractLocalFileConfigSource implements LocalFileConfigSource {

    protected InputStream configInputStream;
    protected Map<String,Object> configMap;

    public AbstractLocalFileConfigSource(InputStream configInputStream) {
        this.configInputStream = configInputStream;
    }

    public AbstractLocalFileConfigSource(){}

    @Override
    public Map<String, Object> getConfMap() {
        if(configMap == null){
            initConfig();
        }
        return configMap;
    }

    protected abstract void initConfig();
}
