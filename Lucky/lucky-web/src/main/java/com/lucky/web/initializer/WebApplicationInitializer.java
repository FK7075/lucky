package com.lucky.web.initializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/12 上午4:43
 */
public interface WebApplicationInitializer {

    void onStartup(ServletContext servletContext) throws ServletException;

    default double priority(){
        return 5;
    }
}
