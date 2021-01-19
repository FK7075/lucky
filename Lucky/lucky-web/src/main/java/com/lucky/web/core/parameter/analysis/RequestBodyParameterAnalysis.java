package com.lucky.web.core.parameter.analysis;

import com.lucky.utils.conversion.JavaConversion;
import com.lucky.utils.file.*;
import com.lucky.web.annotation.RequestBody;
import com.lucky.web.core.Model;
import com.lucky.web.core.RequestBodyParam;
import com.lucky.web.core.parameter.ParameterAnalysisException;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 序列化参数解析
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 16:10
 */
public class RequestBodyParameterAnalysis implements ParameterAnalysis{
    @Override
    public double priority() {
        return 2;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter, String asmParamName) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter, Type genericParameterType, String asmParamName) throws Exception{
        RequestBodyParam requestBodyParam = model.getRequestBodyParam();
        String requestBody = requestBodyParam.getRequestBody();
        String contentType = requestBodyParam.getContentType();
        Class<?> parameterType = parameter.getType();
        if(contentType.startsWith("APPLICATION/JSON")){
            try {
                return model.fromJson(genericParameterType,requestBody);
            }catch (Exception e){
                throw new ParameterAnalysisException("参数转化异常[`application/json`]! 格式错误的请求参数：`"+requestBody+"`");
            }

        }
        if(contentType.startsWith("APPLICATION/XML")){
            try {
                return model.fromXml(genericParameterType,requestBody);
            }catch (Exception e){
                throw new ParameterAnalysisException("参数转化异常[`application/xml`]! 格式错误的请求参数：`"+requestBody+"`");
            }
        }

        try {
            return JavaConversion.strToBasic(requestBody,parameterType);
        }catch (Exception e){
            throw new ParameterAnalysisException("参数转化异常[`"+contentType+"`]! 与预定类型`"+parameterType+"`不兼容的参数：`"+requestBody+"`");
        }
    }
}
