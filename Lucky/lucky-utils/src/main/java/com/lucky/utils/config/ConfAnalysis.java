package com.lucky.utils.config;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午4:41
 */
public interface ConfAnalysis {

    String PREFIX="$LUCKY";
    Engine engine=new Engine();
    JexlContext context=new MapContext();

    Map<String, Object> getMap();

    Object getObject(String prefix);

    default boolean isExpression(String prefix){
        prefix=prefix.trim();
        return prefix.startsWith("${")&&prefix.endsWith("}");
    }
}
