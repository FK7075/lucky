package com.lucky.web.servlet;

import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Module;
import com.lucky.framework.uitls.base.ExceptionUtils;
import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.ControllerAdvice;
import com.lucky.web.annotation.RestController;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.core.Model;
import com.lucky.web.core.parameter.ParameterAnalysisChain;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.enums.Rest;
import com.lucky.web.exception.FileSizeCrossingException;
import com.lucky.web.exception.FileTypeIllegalException;
import com.lucky.web.exception.RequestFileSizeCrossingException;
import com.lucky.web.mapping.DefaultMappingAnalysis;
import com.lucky.web.mapping.ExceptionMappingCollection;
import com.lucky.web.mapping.UrlMapping;
import com.lucky.web.mapping.UrlMappingCollection;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 9:24
 */
public abstract class BaseServlet extends HttpServlet {

    protected static final Logger log = LogManager.getLogger(BaseServlet.class);
    protected ApplicationContext applicationContext;
    protected UrlMappingCollection urlMappingCollection;
    protected ExceptionMappingCollection exceptionMappingCollection;
    protected WebConfig webConfig;

    /**
     * 获取并初始化参数解析链
     * @return 参数解析链
     */
    protected ParameterAnalysisChain getParameterAnalysisChain(){
        ParameterAnalysisChain parameterAnalysisChain = webConfig.getParameterAnalysisChain();
        parameterAnalysisChain.sort();
        return parameterAnalysisChain;
    }

    /**
     * Controller参数的二次加工「校验、加密等处理」
     * @param urlMapping 当前请求的方法映射
     */
    protected void process(UrlMapping urlMapping){
        webConfig.getParameterProcess().processAll(urlMapping);
    }

    /**
     * 处理并返回Controller方法响应的结果
     * @param model 当前请求的Model
     * @param invoke Controller方法执行的结果
     * @param urlMapping 当前请求的方法映射
     * @throws IOException
     */
    protected void response(Model model, Object invoke, UrlMapping urlMapping, Rest rest) throws IOException {
        webConfig.getResponse().jump(model,invoke, urlMapping,rest,webConfig.getPrefix(),webConfig.getSuffix());
    }

    /**
     * 前置处理
     * 处理URL、请求类型和Web上下文的设置
     * @param model 当前请求的Model
     * @throws UnsupportedEncodingException
     */
    protected void beforeDispose(Model model) throws UnsupportedEncodingException {
        webConfig.getMappingPreprocess().beforeDispose(model,webConfig);
    }

    /**
     * 后置处理
     * 1.处理Controller的属性和跨域问题
     * 2.包装文件类型的参数
     * @param model 当前请求的Model
     * @param urlMapping 当前请求的方法映射
     * @throws FileUploadException
     * @throws FileTypeIllegalException
     * @throws FileSizeCrossingException
     * @throws RequestFileSizeCrossingException
     * @throws IOException
     */
    protected void afterDispose(Model model, UrlMapping urlMapping) throws FileUploadException, FileTypeIllegalException, FileSizeCrossingException, RequestFileSizeCrossingException, IOException {
        webConfig.getMappingPreprocess().afterDispose(model,webConfig, urlMapping);
    }

    /**
     * 响应结束后的Web上下文清理
     * @param model 当前请求的Model
     * @param urlMapping 当前请求的方法映射
     */
    protected void setFinally(Model model, UrlMapping urlMapping){
        webConfig.getMappingPreprocess().setFinally(model, urlMapping);
    }

    /**
     * 得到关键的异常
     * @param e 程序抛出的异常
     * @return
     */
    protected Throwable getCauseThrowable(Throwable e){
        return ExceptionUtils.getCauseThrowable(e);
    }

    @Override
    public void destroy() {
        super.destroy();
        AutoScanApplicationContext.create().destroy();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        DefaultMappingAnalysis analysis = new DefaultMappingAnalysis();
        webConfig=WebConfig.getWebConfig();
        applicationContext=AutoScanApplicationContext.create();
        List<Module> controllers = applicationContext.getModuleByAnnotation(Controller.class, RestController.class);
        urlMappingCollection =analysis.analysis(controllers);
        List<Module> controllerAdvices = applicationContext.getModuleByAnnotation(ControllerAdvice.class);
        exceptionMappingCollection=analysis.exceptionAnalysis(controllerAdvices);
        applicationContext.put(new Module("lucky_UrlMappingCollection","url-mapping",urlMappingCollection));
        applicationContext.put(new Module("lucky_ExceptionMappingCollection","exception-mapping",exceptionMappingCollection));
        urlMappingCollection.initRun();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.applyFor(req, resp, RequestMethod.GET);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
        this.applyFor(req, resp, RequestMethod.HEAD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.applyFor(req, resp, RequestMethod.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        this.applyFor(req, resp, RequestMethod.PUT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        this.applyFor(req, resp, RequestMethod.DELETE);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        this.applyFor(req, resp, RequestMethod.OPTIONS);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
        this.applyFor(req, resp, RequestMethod.TRACE);
    }

    protected abstract void applyFor(HttpServletRequest req, HttpServletResponse resp, RequestMethod post);
}
