package com.lucky.framework.confanalysis;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午4:41
 */
public interface ConfAnalysis {

    Map<String, Object> getMap();

//    Object getObject(String prefix);

    default boolean isExpression(String prefix){
        prefix=prefix.trim();
        return prefix.startsWith("${")&&prefix.endsWith("}");
    }
}
