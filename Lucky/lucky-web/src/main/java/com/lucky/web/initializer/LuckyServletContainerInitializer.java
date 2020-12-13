package com.lucky.web.initializer;

import com.lucky.framework.uitls.reflect.ClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servlet容器初始化
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/12 上午5:16
 */
@HandlesTypes(WebApplicationInitializer.class)
public class LuckyServletContainerInitializer implements ServletContainerInitializer {

    private static Logger log= LogManager.getLogger("c.l.w.initializer.LuckyServletContainerInitializer");

    @Override
    public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {

        List<WebApplicationInitializer> initializers = new LinkedList<>();

        if (webAppInitializerClasses != null) {
            for (Class<?> waiClass : webAppInitializerClasses) {
                // Be defensive: Some servlet containers provide us with invalid classes,
                // no matter what @HandlesTypes says...
                if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
                        WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
                    try {
                        initializers.add((WebApplicationInitializer)
                                ClassUtils.newObject(waiClass));
                    }
                    catch (Throwable ex) {
                        throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
                    }
                }
            }
        }

        if (initializers.isEmpty()) {
            log.info("No Lucky WebApplicationInitializer types detected on classpath");
            return;
        }

        log.info(initializers.size() + " Lucky WebApplicationInitializers detected on classpath");
        initializers=initializers.stream().sorted(Comparator.comparing(WebApplicationInitializer::priority)).collect(Collectors.toList());
        for (WebApplicationInitializer initializer : initializers) {
            initializer.onStartup(servletContext);
        }
    }
}
