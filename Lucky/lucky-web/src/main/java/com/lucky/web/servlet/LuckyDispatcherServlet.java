package com.lucky.web.servlet;

import com.lucky.framework.uitls.file.Resources;
import com.lucky.web.core.Model;
import com.lucky.web.core.RequestFilter;
import com.lucky.web.core.WebContext;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.webfile.WebFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 18:52
 */
public class LuckyDispatcherServlet extends BaseServlet {

    private static final Logger log = LogManager.getLogger(LuckyDispatcherServlet.class);

    @Override
    protected void applyFor(HttpServletRequest request, HttpServletResponse response, RequestMethod requestMethod) {
        try {
            Model model = new Model(request, response,
                    this.getServletConfig(), requestMethod, webConfig.getEncoding());

            //前置处理，处理URL、RequestMethod、以及WebContext
            preprocess.dispose(model,webConfig);

            //如果请求的是favicon.ico，直接返回配置文件中配置的favicon.ico文件
            if(ICO.equals(model.getUri())){
                model.getResponse().setContentType("image/x-icon");
                WebFileUtils.preview(model, Resources.getInputStream(webConfig.getFavicon()),ICO);
                return;
            }

            //判断当前请求是否符合IP配置，没有权限的IP地址的请求直接拦截，拒绝访问
            if(!RequestFilter.ipIsPass(model,webConfig)){
                return;
            }

            //静态资源处理
            if(!RequestFilter.isStaticResource(model,webConfig)){
                return;
            }





        } catch (Throwable e) {
            e.printStackTrace();
        }finally {
            WebContext.clearContext();
        }
    }
}
