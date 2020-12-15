package com.lucky.web.initializer;

import com.lucky.web.servlet.LuckyDispatcherServlet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Arrays;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/16 上午2:19
 */
public class LuckyDispatcherServletInitializer implements WebApplicationInitializer{

    private static Logger log= LogManager.getLogger("c.l.w.i.LuckyDispatcherServletInitializer");

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletRegistration.Dynamic luckyDispatcherServlet = servletContext.addServlet("LuckyDispatcherServlet", new LuckyDispatcherServlet());
        luckyDispatcherServlet.setLoadOnStartup(0);
        luckyDispatcherServlet.addMapping("/");
        log.info("WebApplicationInitialize Add Servlet `name=LuckyDispatcherServlet mapping=[/] class=com.lucky.web.servlet.LuckyDispatcherServlet`");
    }
}
