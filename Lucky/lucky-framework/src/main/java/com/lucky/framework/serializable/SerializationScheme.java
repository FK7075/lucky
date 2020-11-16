package com.lucky.framework.serializable;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * 序列化方案
 * @author fk7075
 * @version 1.0
 * @date 2020/11/12 10:45
 */
public interface SerializationScheme extends Serializable {

    String serialization(Object object) throws IOException;

    Object deserialization(Type objectType, String objectStr) throws Exception;
}
