package com.lucky.web.interceptor.builtin;

import com.lucky.utils.proxy.CglibProxy;
import com.lucky.web.core.Model;
import com.lucky.web.enums.Rest;
import com.lucky.web.interceptor.HandlerInterceptor;
import com.lucky.web.interceptor.Interceptor;
import com.lucky.web.interceptor.builtin.annotation.ResultPackage;
import com.lucky.web.mapping.UrlMapping;
import com.lucky.web.result.ExceptionEntity;
import com.lucky.web.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/19 0019 11:29
 */
@Interceptor("/**")
public class RestPackageInterceptor implements HandlerInterceptor {

    private static final Logger log= LoggerFactory.getLogger(RestPackageInterceptor.class);

    @Override
    public Object postHandle(Model model, UrlMapping handler, Object result) throws Exception {
        if(result==null||Rest.NO==handler.getRest()||!isPackage(handler)){
            return result;
        }
        return Result.ok(result);
    }

    @Override
    public void afterCompletion(Model model, UrlMapping handler, Throwable ex) throws Exception {
        if(!isPackage(handler)||ex==null){
            return;
        }
        ExceptionEntity exceptionEntity=new ExceptionEntity((Exception) ex);
        Result result=Result.error(exceptionEntity);
        model.writerJson(result);
        log.error("",ex);
    }

    private boolean isPackage(UrlMapping handler){
        Method method = handler.getMapping();
        ResultPackage resultAnn;
        resultAnn = method.getAnnotation(ResultPackage.class);
        if(resultAnn!=null){
            return resultAnn.value();
        }
        Class<?> originalType = CglibProxy.getOriginalType(handler.getObject().getClass());
        resultAnn = originalType.getAnnotation(ResultPackage.class);
        if(resultAnn!=null){
            return resultAnn.value();
        }
        return false;
    }
}
