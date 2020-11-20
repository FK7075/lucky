package com.lucky.web.core;

import com.lucky.web.conf.WebConfig;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.mapping.Mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

/**
 * 映射预处理器
 * 1.处理URL、请求类型和Web上下文的设置
 * 2.处理Controller的属性和跨域问题
 * 3.请求处理结束后的操作
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 16:28
 */
public interface MappingPreprocess {

    /**
     * 前置处理，处理URL、请求类型和Web上下文的设置
     * @param model 当前请求的Model对象
     * @param config Web配置类
     * @throws UnsupportedEncodingException
     */
    default void beforeDispose(Model model,WebConfig config) throws UnsupportedEncodingException {
        urlDispose(model,config);
        methodDispose(model,config);
        setContext(model,config);
    }

    /**
     * 后置处理，处理Controller的属性和跨域问题
     * @param model 当前请求的Model对象
     * @param mapping 当前请求的映射
     */
    default void afterDispose(Model model, Mapping mapping){
        setField(model,mapping);
        setCross(model,mapping);
    }

    /**
     * URL参数格式化
     * @param model 当前请求的Model对象
     * @param config Web配置类
     * @throws UnsupportedEncodingException
     */
    void urlDispose(Model model, WebConfig config) throws UnsupportedEncodingException;

    /**
     * 请求类型的处理[GET、PUT、DELETE、POST]
     * @param model 当前请求的Model对象
     * @param config Web配置类
     * @throws UnsupportedEncodingException
     */
    void methodDispose(Model model,WebConfig config) throws UnsupportedEncodingException;

    /**
     * Web上下文的设置
     * @param model 当前请求的Model对象
     */
    void setContext(Model model,WebConfig config);

    /**
     * Controller特殊属性的设置[Request、Response、Model....]
     * @param model 当前请求的Model对象
     * @param mapping 当前请求的映射
     */
    void setField(Model model, Mapping mapping);

    /**
     * 跨域设置
     * @param model 当前请求的Model对象
     * @param model 当前请求的Model对象
     * @param mapping 当前请求的映射
     */
    void setCross(Model model, Mapping mapping);

    /**
     * 请求结束后要执行的逻辑
     * @param model 当前请求的Model对象
     * @param mapping 当前请求的映射
     */
    void setFinally(Model model, Mapping mapping);

}
