package com.lucky.utils.config.sources;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * JVM参数配置源
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/5 下午6:24
 */
public class JVMConfigSource implements ConfigSource {

    @Override
    public Map<String, Object> getConfMap() {
        Properties properties = System.getProperties();
        Map<String,Object> map = new HashMap<>(properties.size());
        properties.forEach((k,v)->map.put(k.toString(),v));
        return map;
    }
}
