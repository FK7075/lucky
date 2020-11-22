package com.lucky.web.core.parameter;

import com.lucky.web.core.Model;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/23 上午1:58
 */
public class MultipartFileParameterAnalysis extends FileParameterAnalysis{
    @Override
    public double priority() {
        return 4;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter, String asmParamName) {
        return false;
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter, String asmParamName) throws Exception {
        return null;
    }
}
