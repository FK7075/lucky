package com.lucky.thymeleaf.core;

import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.thymeleaf.conf.ThymeleafConfig;
import com.lucky.thymeleaf.template.ClasspathTemplateResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/1 3:23 上午
 */
public class ThymeleafListener implements ServletContextListener {

   public final static ThymeleafConfig conf=ThymeleafConfig.getThymeleafConfig();
   public final static List<IDialect> dialects= AutoScanApplicationContext.create().getBean(IDialect.class);

    public void contextInitialized(ServletContextEvent sce) {
        TemplateEngine engine = templateEngine(sce.getServletContext());
        for (IDialect obj : dialects) {
            engine.addDialect(obj);
        }
        TemplateEngineUtil.storeTemplateEngine(sce.getServletContext(), engine);
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

    private TemplateEngine templateEngine(ServletContext servletContext) {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver(servletContext));
        return engine;
    }

    private ITemplateResolver templateResolver(ServletContext servletContext) {
        ClasspathTemplateResolver resolver = new ClasspathTemplateResolver();
        resolver.setPrefix(conf.getPrefix());
        resolver.setCharacterEncoding(conf.getEncoding());
        resolver.setSuffix(conf.getSuffix());
        resolver.setCacheable(conf.isCache());
        resolver.setTemplateMode(conf.getModel());
        return resolver;
    }


}
