package com.lucky.boot.startup;

import com.lucky.framework.ApplicationContext;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.annotation.HandlesTypes;
import java.util.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/13 下午6:17
 */
public class ServletContainerInitializerController {

    private static ServletContainerInitializerController servletContainerInitializerController;
    private List<ServletContainerInitializer> servletContainerInitializers;
    private ApplicationContext applicationContext;

    private ServletContainerInitializerController(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
        servletContainerInitializers=new ArrayList<>();
        servletContainerInitializers.add(new LuckyBootServletContainerInitializer());
        ServiceLoader<ServletContainerInitializer> spiServletContainerInitializer
                = ServiceLoader.load(ServletContainerInitializer.class);
        for (ServletContainerInitializer servletContainerInitializer : spiServletContainerInitializer) {
            servletContainerInitializers.add(servletContainerInitializer);
        }
    }

    public void addServletContainerInitializer(ServletContainerInitializer servletContainerInitializer){
        servletContainerInitializers.add(servletContainerInitializer);
    }

    public static ServletContainerInitializerController create(ApplicationContext applicationContext){
        if(servletContainerInitializerController==null){
            servletContainerInitializerController=new ServletContainerInitializerController(applicationContext);
        }
        return servletContainerInitializerController;
    }

    public List<ServletContainerInitializerAndHandlesTypes> getServletContainerInitializerAndHandlesTypes(){
        List<ServletContainerInitializerAndHandlesTypes> scihts=new ArrayList<>();
        servletContainerInitializers.stream().forEach(s->scihts.add(conversion(s)));
        return scihts;
    }

    private ServletContainerInitializerAndHandlesTypes conversion(ServletContainerInitializer servletContainerInitializer){
        ServletContainerInitializerAndHandlesTypes scit=new ServletContainerInitializerAndHandlesTypes();
        scit.setServletContainerInitializer(servletContainerInitializer);
        if(servletContainerInitializer.getClass().isAnnotationPresent(HandlesTypes.class)){
            Class<?>[] types=servletContainerInitializer.getClass()
                    .getAnnotation(HandlesTypes.class).value();
            scit.setHandlesTypes(applicationContext.getClasses(types));
        }
        return scit;
    }

    class ServletContainerInitializerAndHandlesTypes{
        private ServletContainerInitializer servletContainerInitializer;
        private Set<Class<?>> handlesTypes;

        public ServletContainerInitializerAndHandlesTypes() {
            handlesTypes=new HashSet<>();
        }

        public ServletContainerInitializer getServletContainerInitializer() {
            return servletContainerInitializer;
        }

        public void setServletContainerInitializer(ServletContainerInitializer servletContainerInitializer) {
            this.servletContainerInitializer = servletContainerInitializer;
        }

        public Set<Class<?>> getHandlesTypes() {
            return handlesTypes;
        }

        public void setHandlesTypes(Set<Class<?>> handlesTypes) {
            this.handlesTypes = handlesTypes;
        }
    }

}
