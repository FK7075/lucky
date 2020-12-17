package com.lucky.web.core.parameter.enhance;

import com.lucky.utils.dm5.MD5Utils;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.web.annotation.MD5;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 上午1:02
 */
public class ParameterMD5EncryptEnhance implements ParameterEnhance{
    @Override
    public double priority() {
        return 6;
    }

    @Override
    public Object enhance(Parameter parameter, Type genericType, Object runParam, String paramName) {
        if(AnnotationUtils.isExist(parameter, MD5.class)){
            MD5 md5 = parameter.getAnnotation(MD5.class);
            return MD5Utils.md5(runParam.toString(),md5.salt(),md5.cycle(),md5.capital());
        }
        return runParam;
    }
}
