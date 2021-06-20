package com.lucky.boot.startup;

import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Module;
import com.lucky.framework.welcome.JackLamb;
import com.lucky.utils.base.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/3 0003 11:33
 */
public class LuckyApplication {

    private static Logger log;
    private static final RuntimeMXBean mxb = ManagementFactory.getRuntimeMXBean();
    static {
        System.setProperty("log4j.skipJansi","false");
    }

    public static void run(Class<?> applicationClass,String[] args){
        EmbeddedTomcat tomcat = null;
        long start;
        try {
            JackLamb.welcome();
            start = System.currentTimeMillis();
            log= LoggerFactory.getLogger(applicationClass);
            String pid = mxb.getName().split("@")[0];
            MDC.put("pid",pid);
            String classpath= Assert.isNotNull(applicationClass.getClassLoader().getResource(""))
                    ?applicationClass.getClassLoader().getResource("").getPath():applicationClass.getResource("").getPath();
            log.info("Starting {} on localhost with PID {} ({} started by {} in {})"
                    ,applicationClass.getSimpleName()
                    ,pid
                    ,classpath
                    ,System.getProperty("user.name")
                    ,System.getProperty("user.dir"));
            tomcat=new EmbeddedTomcat(applicationClass,args);

        }catch (Exception e){
            throw new TomcatStartException(e);
        }
        tomcat.run();
        long end = System.currentTimeMillis();
        log.info("Started {} in {} seconds (JVM running for {})"
                ,applicationClass.getSimpleName()
                ,(end-start)/1000.0
                ,mxb.getUptime()/1000.0);
    }
}

