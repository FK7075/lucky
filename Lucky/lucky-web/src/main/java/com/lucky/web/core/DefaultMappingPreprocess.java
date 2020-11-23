package com.lucky.web.core;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.framework.uitls.reflect.FieldUtils;
import com.lucky.web.annotation.CrossOrigin;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.core.parameter.AnnotationFileParameterAnalysis;
import com.lucky.web.core.parameter.MultipartFileParameterAnalysis;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.exception.FileSizeCrossingException;
import com.lucky.web.exception.FileTypeIllegalException;
import com.lucky.web.exception.RequestFileSizeCrossingException;
import com.lucky.web.mapping.Mapping;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Lucky提供的默认映射预处理器
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 16:34
 */
public class DefaultMappingPreprocess implements MappingPreprocess {

    private static final String[] SUFFIX={".lucky",".do",".xfl",".fk",".cad",".wl"};

    @Override
    public void urlDispose(Model model, WebConfig config) throws UnsupportedEncodingException {
        String url=model.getUri();
        if(url.contains(";")){
            url=url.substring(0,url.indexOf(";"));
        }
        url = java.net.URLDecoder.decode(new String(url.getBytes(model.getEncod()), model.getResponse().getCharacterEncoding()),  model.getResponse().getCharacterEncoding());
        String context = model.getRequest().getContextPath();
        url = url.replace(context, "");
        url=url.endsWith("/")?url.substring(0,url.length()-1):url;
        if (Assert.strEndsWith(url,SUFFIX)) {
            //Lucky默认可以使用的后缀
            url = url.substring(0, url.lastIndexOf("."));
        }
        model.setUri(url);
    }

    @Override
    public void methodDispose(Model model,WebConfig config) throws UnsupportedEncodingException {
        if (config.isPostChangeMethod()&&model.getRequestMethod() == RequestMethod.POST) {
            String hihMeth = model.getParameter("_method");
            if (hihMeth != null) {
                if ("POST".equalsIgnoreCase(hihMeth)) {
                    model.setRequestMethod(RequestMethod.POST);
                } else if ("GET".equalsIgnoreCase(hihMeth)) {
                    model.setRequestMethod(RequestMethod.GET);
                } else if ("PUT".equalsIgnoreCase(hihMeth)) {
                    model.setRequestMethod(RequestMethod.PUT);
                } else if ("DELETE".equalsIgnoreCase(hihMeth)) {
                    model.setRequestMethod(RequestMethod.DELETE);
                }
            }
        }
    }

    @Override
    public void setContext(Model model, WebConfig config) {
        WebContext luckyWebContext = WebContext.createContext();
        luckyWebContext.setRequest(model.getRequest());
        luckyWebContext.setResponse(model.getResponse());
        luckyWebContext.setSession(model.getSession());
        luckyWebContext.setApplication(model.getServletContext());
        luckyWebContext.setRequestMethod(model.getRequestMethod());
        luckyWebContext.setServletConfig(model.getServletConfig());
        WebContext.setContext(luckyWebContext);
    }

    @Override
    public void setField(Model model, Mapping mapping) {
        Object controller=mapping.getController();
        Class<?> controllerClass=controller.getClass();
        Field[] fields= ClassUtils.getAllFields(controllerClass);
        for(Field field:fields) {
            if(Model.class.isAssignableFrom(field.getType())) {
                FieldUtils.setValue(controller,field,model);
            }else if(HttpSession.class.isAssignableFrom(field.getType())) {
                FieldUtils.setValue(controller,field,model.getSession());
            }else if(ServletRequest.class.isAssignableFrom(field.getType())) {
                FieldUtils.setValue(controller,field,model.getRequest());
            }else if(ServletResponse.class.isAssignableFrom(field.getType())) {
                FieldUtils.setValue(controller,field,model.getResponse());
            }else if(ServletContext.class.isAssignableFrom(field.getType())) {
                FieldUtils.setValue(controller,field,model.getServletContext());
            }else if(ServletConfig.class.isAssignableFrom(field.getType())){
                FieldUtils.setValue(controller,field,model.getServletConfig());
            }else{
                continue;
            }
        }
    }

    @Override
    public void setCross(Model model, Mapping mapping) {
        Object controller=mapping.getController();
        Method controllerMethod=mapping.getMapping();
        Class<?> controllerClass = controller.getClass();
        if(!(AnnotationUtils.isExist(controllerMethod,CrossOrigin.class)
                ||AnnotationUtils.isExist(controllerClass,CrossOrigin.class))){
            return;
        }
        CrossOrigin crso= AnnotationUtils.get(controllerClass,CrossOrigin.class);
        if(AnnotationUtils.isExist(controllerMethod,CrossOrigin.class)){
            crso=AnnotationUtils.get(controllerMethod,CrossOrigin.class);
        }
        HttpServletRequest request = model.getRequest();
        HttpServletResponse response = model.getResponse();
        String url = request.getHeader("Origin");
        String[] url_v = crso.value();
        String[] url_o = crso.origins();
        if ((url_v.length != 0 && url_o.length != 0)
                && (!Arrays.asList(url_v).contains(url) && !Arrays.asList(url_o).contains(url))) {
            url = "fk-xfl-wl";
        }
        String isCookie = "false";
        if (crso.allowCredentials()) {
            isCookie = "true";
        }
        response.setHeader("Access-Control-Allow-Origin", url);
        response.setHeader("Access-Control-Allow-Methods", crso.method());
        response.setHeader("Access-Control-Max-Age", crso.maxAge() + "");
        response.setHeader("Access-Control-Allow-Headers", crso.allowedHeaders());
        response.setHeader("Access-Control-Allow-Credentials", isCookie);
        response.setHeader("XDomainRequestAllowed", "1");
    }

    @Override
    public void setFinally(Model model, Mapping mapping) {
        WebContext.clearContext();
    }
}
