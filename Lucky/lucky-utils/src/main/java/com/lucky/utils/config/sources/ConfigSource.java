package com.lucky.utils.config.sources;

import java.util.Map;

/**
 * 配置源
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/5 下午6:17
 */
public interface ConfigSource {

    Map<String,Object> getConfMap();

    class ConfigSourceParsingException extends RuntimeException{

        public ConfigSourceParsingException(Exception e,String msg){
            super(msg,e);
        }
    }

}
