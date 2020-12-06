package com.lucky.web.core.parameter;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.conversion.JavaConversion;
import com.lucky.framework.uitls.file.FileUtils;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.web.annotation.RequestBody;
import com.lucky.web.core.Model;
import com.lucky.web.enums.Rest;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
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
        HttpServletRequest request = model.getRequest();
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringWriter sw = new StringWriter();
        FileUtils.copy(br,sw);
        String requestBody = sw.toString();
        String contentType = new ServletRequestContext(request).getContentType().toUpperCase();
        Class<?> parameterType = parameter.getType();
        if(contentType.startsWith("APPLICATION/JSON")){
            try {
                return model.fromJson(genericParameterType,requestBody);
            }catch (Exception e){
                throw new ParameterAnalysisException("参数转化异常[`application/json`]! 格式错误的请求参数：`"+requestBody+"`",e);
            }

        }
        if(contentType.startsWith("APPLICATION/XML")){
            try {
                return model.fromXml(genericParameterType,requestBody);
            }catch (Exception e){
                throw new ParameterAnalysisException("参数转化异常[`application/xml`]! 格式错误的请求参数：`"+requestBody+"`",e);
            }
        }

        try {
            return JavaConversion.strToBasic(requestBody,parameterType);
        }catch (Exception e){
            throw new ParameterAnalysisException("参数转化异常[`"+contentType+"`]! 与预定类型`"+parameterType+"`不兼容的参数：`"+requestBody+"`",e);
        }
    }
}
