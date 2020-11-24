package com.lucky.web.servlet;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.file.Resources;
import com.lucky.web.core.Model;
import com.lucky.web.core.RequestFilter;
import com.lucky.web.core.WebContext;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.mapping.ExceptionMapping;
import com.lucky.web.mapping.Mapping;
import com.lucky.web.webfile.WebFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 18:52
 */
public class LuckyDispatcherServlet extends BaseServlet {

    private static final Logger log = LogManager.getLogger(LuckyDispatcherServlet.class);

    @Override
    protected void applyFor(HttpServletRequest request, HttpServletResponse response, RequestMethod requestMethod) {
        Model model=null;
        Mapping mapping=null;
        try {
          model = new Model(request, response,
                    this.getServletConfig(), requestMethod, webConfig.getEncoding());

            //前置处理，处理URL、RequestMethod、以及WebContext
            beforeDispose(model);

            //全局IP配置校验、静态资源、favicon.ico等简单请求的处理
            if(!RequestFilter.filter(model,webConfig)){
                return;
            }

            mapping = mappingCollection.getMapping(model);
            // URL、IP、RequestMethod校验
            if(Assert.isNull(mapping)){
                return;
            }

            //后置处理，处理Controller的属性和跨域问题以及包装文件类型的参数
            afterDispose(model,mapping);

            //获取执行参数
            Object[] runParam=getParameterAnalysisChain().analysis(model,mapping);

            //执行Controller方法并获取返回结果
            Object invoke = mapping.invoke(runParam);

            //响应请求结果
            response(model,invoke,mapping);


        } catch (Throwable e) {
            e=getCauseThrowable(e);
            ExceptionMapping exceptionMapping = exceptionMappingCollection.getExceptionMapping(mapping, e);
            if(exceptionMapping==null){
                model.e500(e);
            }

        }finally {
            setFinally(model,mapping);
        }
    }
}
