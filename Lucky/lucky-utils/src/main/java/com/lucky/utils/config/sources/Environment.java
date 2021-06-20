package com.lucky.utils.config.sources;

import java.util.List;
import java.util.Map;

/**
 * 环境变量
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/5 下午6:29
 */
public interface Environment {

    List<Map<String,Object>> getAllConfigMap();

    Map<String,Object> getEvn();

    Map<String,Object> getProperties();


}
