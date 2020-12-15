package com.lucky.boot.startup;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.welcome.JackLamb;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

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

    public static void run(Class<?> applicationClass,String[] args){
        JackLamb.welcome();
        long start = System.currentTimeMillis();
        log= LogManager.getLogger(applicationClass);
        String pid = mxb.getName().split("@")[0];
        ThreadContext.put("pid",pid);
        String classpath= Assert.isNotNull(applicationClass.getClassLoader().getResource(""))
                ?applicationClass.getClassLoader().getResource("").getPath():applicationClass.getResource("").getPath();
        log.info("Starting {} on localhost with PID {} ({} started by {} in {})"
                ,applicationClass.getSimpleName()
                ,pid
                ,classpath
                ,System.getProperty("user.name")
                ,System.getProperty("user.dir"));
        EmbeddedTomcat tomcat=new EmbeddedTomcat(applicationClass,args);
        tomcat.run();
        long end = System.currentTimeMillis();
        log.info("Started {} in {} seconds (JVM running for {})"
                ,applicationClass.getSimpleName()
                ,(end-start)/1000.0
                ,mxb.getUptime()/1000.0);
        tomcat.await();
    }
}

