package com.lucky.web.core.parameter;

import com.lucky.web.core.Model;
import com.lucky.web.webfile.MultipartFile;

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
        Class<?> parameterClass = parameter.getType();
        String paramName = getParamName(parameter, asmParamName);
        boolean isKey=model.multipartFileMapContainsKey(paramName);
        return isKey && (parameterClass== MultipartFile.class||parameterClass== MultipartFile[].class);
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter, String asmParamName) throws Exception {
        String paramName = getParamName(parameter, asmParamName);
        Class<?> parameterClass = parameter.getType();
        MultipartFile[] multipartFileArray = model.getMultipartFileArray(paramName);
        if(parameterClass== MultipartFile.class){
            return multipartFileArray[multipartFileArray.length-1];
        }else if(parameterClass== MultipartFile[].class){
            return multipartFileArray;
        }else{
            return null;
        }
    }
}
