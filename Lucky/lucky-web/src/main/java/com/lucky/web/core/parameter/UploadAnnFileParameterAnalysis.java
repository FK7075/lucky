package com.lucky.web.core.parameter;

import com.lucky.web.annotation.Upload;
import com.lucky.web.core.Model;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/23 上午1:57
 */
public class UploadAnnFileParameterAnalysis extends FileParameterAnalysis{
    @Override
    public double priority() {
        return 3;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter, String asmParamName) {
        if(method.isAnnotationPresent(Upload.class)){
            String paramName = getParamName(parameter, asmParamName);
            Class<?> parameterClass = parameter.getType();
            Upload upload=method.getAnnotation(Upload.class);
            List<String> names= Arrays.asList(upload.names());
            boolean isKey=names.contains(paramName);
            return isKey&&(parameterClass==String.class||parameterClass==String[].class
                    ||parameterClass== File.class||parameterClass==File[].class);
        }
        return false;
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter, String asmParamName) throws Exception {
        String paramName = getParamName(parameter, asmParamName);
        Class<?> parameterClass = parameter.getType();
        File[] uploadFileArray = model.getUploadFileArray(paramName);
        if(parameterClass==String.class){
            return uploadFileArray[uploadFileArray.length-1].getAbsolutePath();
        }else if(parameterClass==String[].class){
            String[] absolutePaths=new String[uploadFileArray.length];
            List<String> collect = Arrays.stream(uploadFileArray).map(f -> f.getAbsolutePath()).collect(Collectors.toList());
            collect.toArray(absolutePaths);
            return absolutePaths;
        }else if(parameterClass==File.class){
            return uploadFileArray[uploadFileArray.length-1];
        }else if(parameterClass==File[].class){
            return uploadFileArray;
        }
        return null;
    }
}

