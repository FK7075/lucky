package com.lucky.thymeleaf.integration;

import com.lucky.thymeleaf.core.ThymeleafListener;
import com.lucky.web.initializer.WebApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/21 0021 15:07
 */
public class ThymeleafWebApplicationInitializer implements WebApplicationInitializer {

    private static final Logger log= LoggerFactory.getLogger("c.l.t.i.ThymeleafWebApplicationInitializer");

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(new ThymeleafListener());
        log.info("ThymeleafWebApplicationInitializer Add Listener `name = ThymeleafWebApplicationInitializer class = com.lucky.thymeleaf.integration.ThymeleafWebApplicationInitializer`");
    }

}
