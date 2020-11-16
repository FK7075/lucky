package com.lucky.framework.serializable.implement;

import com.lucky.framework.serializable.XMLSerializationScheme;
import com.lucky.framework.serializable.implement.xml.LXML;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/12 11:51
 */
public class XtreamSerializationScheme implements XMLSerializationScheme {

    private static LXML lxml=new LXML();

    @Override
    public String serialization(Object object) throws IOException {
        return lxml.toXml(object);
    }

    @Override
    public Object deserialization(Type objectType, String objectStr) throws Exception {
        return lxml.fromXml(objectStr);
    }
}
