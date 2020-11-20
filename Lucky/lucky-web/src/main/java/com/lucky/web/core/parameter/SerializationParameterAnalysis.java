package com.lucky.web.core.parameter;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.web.annotation.RequestBody;
import com.lucky.web.core.Model;
import com.lucky.web.enums.Rest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 序列化参数解析
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 16:10
 */
public class SerializationParameterAnalysis implements ParameterAnalysis{
    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter, String asmParamName) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter, String asmParamName) throws Exception{
        String paramName = getParamName(parameter, asmParamName);
        String paramValue;
        if(model.getParameterSize()==1){
            paramValue=model.getDefaultParameterValue();
        }else{
            paramValue = model.getParameter(paramName);
        }

        if(Assert.isNull(paramValue)){
            return null;
        }
        Rest rest= AnnotationUtils.get(parameter,RequestBody.class).value();
        Class<?> parameterType = parameter.getType();
        if(rest==Rest.JSON){
            return model.fromJson(parameterType,paramValue);
        }else if(rest==Rest.JSON){
            return model.fromXml(parameterType,paramValue);
        }else {
            return null;
        }
    }
}
