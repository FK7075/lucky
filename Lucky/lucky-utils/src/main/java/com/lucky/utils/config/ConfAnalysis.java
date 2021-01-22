package com.lucky.utils.config;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午4:41
 */
public interface ConfAnalysis {

    Map<String, Object> getMap();

    Object getObject(Object prefix);

}
