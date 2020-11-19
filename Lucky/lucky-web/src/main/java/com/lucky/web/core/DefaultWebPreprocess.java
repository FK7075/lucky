package com.lucky.web.core;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.enums.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 16:34
 */
public class DefaultWebPreprocess implements WebPreprocess {

    private static final String[] SUFFIX={".lucky",".do",".xfl",".fk",".cad",".wl"};

    @Override
    public void urlDispose(Model model, WebConfig webConfig) throws UnsupportedEncodingException {
        String url=model.getUri();
        if(url.contains(";")){
            url=url.substring(0,url.indexOf(";"));
        }
        url = java.net.URLDecoder.decode(new String(url.getBytes(webConfig.getEncoding()), model.getResponse().getCharacterEncoding()),  model.getResponse().getCharacterEncoding());
        String context = model.getRequest().getContextPath();
        url = url.replace(context, "");
        if (Assert.strEndsWith(url,SUFFIX)) {
            //Lucky默认可以使用的后缀
            url = url.substring(0, url.lastIndexOf("."));
        }
        model.setUri(url);
    }

    @Override
    public void methodDispose(Model model, WebConfig webConfig) throws UnsupportedEncodingException {
        model.getRequest().setCharacterEncoding("utf8");
        model.getResponse().setCharacterEncoding("utf8");
        model.getResponse().setHeader("Content-Type", "text/html;charset=utf-8");
        if (webConfig.isPostChangeMethod()&&model.getRequestMethod() == RequestMethod.POST) {
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
    public void setContext(Model model) {
        WebContext luckyWebContext = WebContext.createContext();
        luckyWebContext.setRequest(model.getRequest());
        luckyWebContext.setResponse(model.getResponse());
        luckyWebContext.setSession(model.getSession());
        luckyWebContext.setApplication(model.getServletContext());
        luckyWebContext.setRequestMethod(model.getRequestMethod());
        luckyWebContext.setServletConfig(model.getServletConfig());
        WebContext.setContext(luckyWebContext);
    }
}
