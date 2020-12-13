package com.lucky.boot.startup;

import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.welcome.JackLamb;
import com.lucky.boot.conf.ServerConfig;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/3 0003 11:33
 */
public class LuckyBootApplication {

    private static Logger log;
    private static final RuntimeMXBean mxb = ManagementFactory.getRuntimeMXBean();
    static {
        System.setProperty("log4j.skipJansi","false");
    }

    public static void run(Class<?> applicationClass,String[] args) {
        long start = System.currentTimeMillis();
        log= LogManager.getLogger(applicationClass);
        JackLamb.welcome();
        String pid = mxb.getName().split("@")[0];
        ThreadContext.put("pid", pid);
        String classpath= Assert.isNotNull(applicationClass.getClassLoader().getResource(""))
                ?applicationClass.getClassLoader().getResource("").getPath():applicationClass.getResource("").getPath();
        log.info("Starting {} on localhost with PID {} ({} started by {} in {})"
                ,applicationClass.getSimpleName()
                ,pid
                ,classpath
                ,System.getProperty("user.name")
                ,System.getProperty("user.dir"));
        ApplicationContext applicationContext= AutoScanApplicationContext.create(applicationClass);
        doShutDownWork(applicationContext);
        ServerConfig serverConf=RunParam.withConf(args);
        run(applicationClass,applicationContext,serverConf,start);
    }

    private static void run(Class<?> applicationClass, ApplicationContext applicationContext, ServerConfig serverConf, long start) {
        log= LogManager.getLogger(applicationClass);
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(serverConf.getPort());
        tomcat.setBaseDir(serverConf.getBaseDir());
        tomcat.getHost().setAutoDeploy(serverConf.isAutoDeploy());
        if (Assert.isNotNull(serverConf.getClosePort())) {
            tomcat.getServer().setPort(serverConf.getClosePort());
        }
        if (Assert.isNotNull(serverConf.getShutdown())) {
            tomcat.getServer().setShutdown(serverConf.getShutdown());
        }
        StandardContext context = new StandardContext();
        context.setSessionTimeout(serverConf.getSessionTimeout());
        context.setPath(serverConf.getContextPath());
        context.setReloadable(serverConf.isReloadable());
        String docBase = serverConf.getDocBase();
        if(docBase!=null){
            File docFile=new File(docBase);
            if(!docFile.exists()) {
                docFile.mkdirs();
            }
            context.setDocBase(docBase);
        }
        context.setSessionCookieName("LUCKY-SESSION-ID");
        context.addLifecycleListener(new Tomcat.FixContextListener());
        context.addLifecycleListener(new Tomcat.DefaultWebXmlListener());
        ServletContainerInitializerController initializerController = ServletContainerInitializerController.create(applicationContext);
        List<ServletContainerInitializerController.ServletContainerInitializerAndHandlesTypes> servletContainerInitializerAndHandlesTypes
                = initializerController.getServletContainerInitializerAndHandlesTypes();
        for (ServletContainerInitializerController.ServletContainerInitializerAndHandlesTypes initializerAndHandlesType : servletContainerInitializerAndHandlesTypes) {
            context.addServletContainerInitializer(initializerAndHandlesType.getServletContainerInitializer(),
                    initializerAndHandlesType.getHandlesTypes());
        }
//        context.addServletContainerInitializer(new LuckyServletContainerInitializer(applicationContext), null);
//        Set<Class<?>> websocketSet=new HashSet<>();
//        applicationContext.getModuleByAnnotation(ServerEndpoint.class).stream().forEach(m->websocketSet.add(m.getOriginalType()));
//        applicationContext.getModule(ServerApplicationConfig.class, Endpoint.class).stream().forEach(m->websocketSet.add(m.getOriginalType()));
//        context.addServletContainerInitializer(new WsSci(), websocketSet);
        tomcat.getHost().addChild(context);
        try {
            tomcat.getConnector();
            tomcat.init();
            tomcat.start();
            long end = System.currentTimeMillis();
            log.info("Started {} in {} seconds (JVM running for {})"
                    ,applicationClass.getSimpleName()
                    ,(end-start)/1000.0
                    ,mxb.getUptime()/1000.0);
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    private static void doShutDownWork(ApplicationContext applicationContext) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            applicationContext.destroy();
        }));
    }
}

