package com.lucky.redis.serialization;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 序列化方案
 * @author fk
 * @version 1.0
 * @date 2020/12/10 0010 16:35
 */
public interface RedisSerialization {

    /**
     * 序列化方案
     * @param key KEY
     * @return
     * @throws IOException
     */
    String serialization(Object key) throws IOException;

    /**
     * 反序列化方案
     * @param objectType Java类型
     * @param objectStr 待序列化的Str
     * @return
     * @throws Exception
     */
    Object deserialization(Type objectType, String objectStr) throws Exception;

}
