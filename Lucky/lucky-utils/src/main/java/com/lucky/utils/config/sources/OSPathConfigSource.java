package com.lucky.utils.config.sources;

import java.util.HashMap;
import java.util.Map;

/**
 * 环境变量配置源
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/5 下午6:24
 */
public class OSPathConfigSource implements ConfigSource {
    @Override
    public Map<String, Object> getConfMap() {
        Map<String, String> envs = System.getenv();
        Map<String,Object> map = new HashMap<>(envs.size());
        envs.forEach(map::put);
        return map;
    }
}
