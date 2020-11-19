package com.lucky.web.core;

import com.lucky.web.conf.WebConfig;
import com.lucky.web.enums.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 16:28
 */
public interface WebPreprocess {

    default void dispose(Model model, WebConfig webConfig) throws UnsupportedEncodingException {
        urlDispose(model,webConfig);
        methodDispose(model,webConfig);
        setContext(model);
    }

    void urlDispose(Model model, WebConfig webConfig) throws UnsupportedEncodingException;

    void methodDispose(Model model,WebConfig webConfig) throws UnsupportedEncodingException;

    void setContext(Model model);

}
