package com.lucky.web.servlet;

import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.RestController;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.core.DefaultMappingPreprocess;
import com.lucky.web.core.MappingPreprocess;
import com.lucky.web.core.Model;
import com.lucky.web.core.parameter.ParameterAnalysisChain;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.enums.Rest;
import com.lucky.web.exception.FileSizeCrossingException;
import com.lucky.web.exception.FileTypeIllegalException;
import com.lucky.web.exception.RequestFileSizeCrossingException;
import com.lucky.web.mapping.DefaultMappingAnalysis;
import com.lucky.web.mapping.Mapping;
import com.lucky.web.mapping.MappingCollection;
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
    protected MappingCollection mappingCollection;
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
     * 处理并返回Controller方法响应的结果
     * @param model 当前请求的Model
     * @param invoke Controller方法执行的结果
     * @param mapping 当前响应的映射
     * @throws IOException
     */
    protected void response(Model model, Object invoke, Mapping mapping) throws IOException {
        webConfig.getResponse().jump(model,invoke,mapping,webConfig.getPrefix(),webConfig.getSuffix());
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
     * @param mapping 当前请求的方法映射
     * @throws FileUploadException
     * @throws FileTypeIllegalException
     * @throws FileSizeCrossingException
     * @throws RequestFileSizeCrossingException
     * @throws IOException
     */
    protected void afterDispose(Model model, Mapping mapping) throws FileUploadException, FileTypeIllegalException, FileSizeCrossingException, RequestFileSizeCrossingException, IOException {
        webConfig.getMappingPreprocess().afterDispose(model,webConfig,mapping);
    }

    /**
     * 响应结束后的Web上下文清理
     * @param model 当前请求的Model
     * @param mapping 当前请求的方法映射
     */
    protected void setFinally(Model model,Mapping mapping){
        webConfig.getMappingPreprocess().setFinally(model,mapping);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        webConfig=WebConfig.getWebConfig();
        applicationContext=AutoScanApplicationContext.create();
        List<Object> controllers = applicationContext.getBeanByAnnotation(Controller.class, RestController.class);
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
