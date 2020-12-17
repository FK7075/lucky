package com.lucky.web.core.parameter.enhance;

import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.web.annotation.Escape;
import com.lucky.web.enums.EscapeType;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 上午1:05
 */
public class ParameterEscapeEnhance implements ParameterEnhance{
    @Override
    public double priority() {
        return 3;
    }

    @Override
    public Object enhance(Parameter parameter, Type genericType, Object runParam, String paramName) {
        if(AnnotationUtils.isExist(parameter, Escape.class)){
            Escape escape = parameter.getAnnotation(Escape.class);
            EscapeType escapeType = escape.value();
            runParam=escapeType.escape(runParam.toString());
        }
        return runParam;
    }
}
