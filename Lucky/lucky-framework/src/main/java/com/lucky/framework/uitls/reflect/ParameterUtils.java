package com.lucky.framework.uitls.reflect;

import com.lucky.framework.annotation.Param;
import com.lucky.framework.uitls.base.Assert;

import java.lang.reflect.Parameter;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/16 13:05
 */
public abstract class ParameterUtils {

    public static String getParamName(Parameter param, String paramName){
        if(param.isAnnotationPresent(Param.class)){
            Param rp = param.getAnnotation(Param.class);
            if(Assert.isBlankString(rp.value())) {
                return paramName==null?param.getName():paramName;
            }
            return rp.value();
        }
        return paramName==null?param.getName():paramName;
    }
}
