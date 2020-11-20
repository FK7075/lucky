package com.lucky.web.core.parameter;

import com.lucky.web.annotation.RequestBody;
import com.lucky.web.core.Model;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 包装类型解析
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 9:18
 */
public class PojoTypeParameterAnalysis implements ParameterAnalysis{

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter, String asmParamName) {
        if(parameter.isAnnotationPresent(RequestBody.class)){
            return false;
        }

        return false;
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter,String asmParamName) {
        return null;
    }
}
