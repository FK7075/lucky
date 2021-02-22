package com.lucky.redis.core;

import com.lucky.framework.serializable.SerializationScheme;
import com.lucky.utils.conversion.JavaConversion;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/2/19 下午7:48
 */
public class StringSerializationScheme implements SerializationScheme {

    @Override
    public String serialization(Object object) throws IOException {
        return object.toString();
    }

    @Override
    public Object deserialization(Type objectType, String objectStr) throws Exception {
        return JavaConversion.strToBasic(objectStr,objectType);
    }
}
