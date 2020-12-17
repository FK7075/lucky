package com.lucky.web.core.parameter;

import com.lucky.utils.reflect.ClassUtils;
import com.lucky.web.core.Model;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * HTTP参数解析
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 11:48
 */
public class HttpParameterAnalysis implements ParameterAnalysis{

    private final static Class<?>[] HTTP_CLASS=
            new Class[]{ServletRequest.class, ServletResponse.class, HttpSession.class,
                    ServletContext.class, ServletConfig.class,Model.class};


    @Override
    public double priority() {
        return 0;
    }

    @Override
    public boolean can(Model model, Method method, Parameter parameter,String asmParamName) {
        return ClassUtils.isAssignableFromArrayOr(parameter.getType(),HTTP_CLASS);
    }

    @Override
    public Object analysis(Model model, Method method, Parameter parameter, Type genericParameterType, String asmParamName) {
        Class<?> parameterType = parameter.getType();
        if (ServletRequest.class.isAssignableFrom(parameterType)) {
            return model.getRequest();
        } else if (HttpSession.class.isAssignableFrom(parameterType)) {
            return model.getSession();
        } else if (ServletResponse.class.isAssignableFrom(parameterType)) {
            return model.getResponse();
        } else if (ServletContext.class.isAssignableFrom(parameterType)) {
            return model.getServletContext();
        } else if(ServletConfig.class.isAssignableFrom(parameterType)){
            return model.getServletConfig();
        }else {
            return model;
        }
    }
}
