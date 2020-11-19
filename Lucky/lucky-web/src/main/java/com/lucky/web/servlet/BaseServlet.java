package com.lucky.web.servlet;

import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.annotation.Controller;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.core.DefaultWebPreprocess;
import com.lucky.web.core.WebPreprocess;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.mapping.DefaultMappingAnalysis;
import com.lucky.web.mapping.MappingCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 9:24
 */
public abstract class BaseServlet extends HttpServlet {

    protected static final Logger log = LogManager.getLogger(BaseServlet.class);
    protected final static String ICO ="/favicon.ico";
    protected ApplicationContext applicationContext;
    protected MappingCollection mappingCollection;
    protected WebConfig webConfig;
    protected WebPreprocess preprocess;

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        preprocess=new DefaultWebPreprocess();
        webConfig=WebConfig.getWebConfig();
        applicationContext=AutoScanApplicationContext.create();
        List<Object> controllers = applicationContext.getBeanByAnnotation(Controller.class);
        mappingCollection=new DefaultMappingAnalysis().analysis(controllers);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.applyFor(req, resp, RequestMethod.GET);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.applyFor(req, resp, RequestMethod.HEAD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.applyFor(req, resp, RequestMethod.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.applyFor(req, resp, RequestMethod.PUT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.applyFor(req, resp, RequestMethod.DELETE);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.applyFor(req, resp, RequestMethod.OPTIONS);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.applyFor(req, resp, RequestMethod.TRACE);
    }

    protected abstract void applyFor(HttpServletRequest req, HttpServletResponse resp, RequestMethod post);
}
