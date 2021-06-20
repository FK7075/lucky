package com.lucky.utils.config.sources;

import com.lucky.utils.jexl.JexlEngineUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/14 上午10:03
 */
public class DefaultEnvironment implements Environment{

    private  JexlEngineUtil jexlEngineUtil;
    private final Map<String,Object> environmentMap = new ConcurrentHashMap<>(225);



    @Override
    public List<Map<String, Object>> getAllConfigMap() {
        return null;
    }

    @Override
    public Map<String, Object> getEvn() {
        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        return null;
    }
}
