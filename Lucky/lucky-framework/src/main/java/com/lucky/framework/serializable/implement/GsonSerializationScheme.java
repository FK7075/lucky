package com.lucky.framework.serializable.implement;

import com.google.gson.reflect.TypeToken;
import com.lucky.framework.serializable.JSONSerializationScheme;
import com.lucky.framework.serializable.implement.json.LSON;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/12 10:58
 */
public class GsonSerializationScheme implements JSONSerializationScheme {

    private static LSON lson=new LSON();

    @Override
    public String serialization(Object object) throws IOException {
        return lson.toJson(object);
    }

    @Override
    public Object deserialization(Type objectType, String objectStr) throws Exception {
        return lson.fromJson(objectType,objectStr);
    }
}
