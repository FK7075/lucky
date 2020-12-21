package com.lucky.thymeleaf.core;

import org.thymeleaf.TemplateEngine;

import javax.servlet.ServletContext;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/1 3:22 上午
 */
public class TemplateEngineUtil {

    private static final String TEMPLATE_ENGINE_ATTR = "com.yiibai.thymeleaf3.TemplateEngineInstance";

    public static void storeTemplateEngine(ServletContext context, TemplateEngine engine) {
        context.setAttribute(TEMPLATE_ENGINE_ATTR, engine);
    }

    public static TemplateEngine getTemplateEngine(ServletContext context) {
        TemplateEngine engine=(TemplateEngine) context.getAttribute(TEMPLATE_ENGINE_ATTR);
        return engine;
    }


}
