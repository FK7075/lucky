package com.lucky.boot.startup;

import com.lucky.boot.conf.ServerConfig;
import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.utils.base.Assert;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.apache.coyote.http11.Http11NioProtocol;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/15 0015 9:08
 */
public class EmbeddedTomcat {

    private final Tomcat tomcat;
    private final ApplicationContext applicationContext;
    private final ServerConfig serverConf;
    private final Context context;
    private Connector connector;

    public EmbeddedTomcat(Class<?> applicationClass,String[] args){
        applicationContext= AutoScanApplicationContext.create(applicationClass);
        serverConf=RunParam.withConf(args);
        context = new StandardContext();
        tomcat=new Tomcat();
    }

    /**
     * 启动服务
     */
    public void run(){
        doShutDownWork();
        conf();
        init();
        start();
    }

    /**
     * 开始监听端口
     */
    public void await(){
        tomcat.getServer().await();
    }

    /**
     * 配置
     */
    private void conf(){
        tomcatConf();
        contextConf();
        tomcat.getHost().addChild(context);
    }

    private void connectorConf() {
        Http11NioProtocol handler  = (Http11NioProtocol) connector.getProtocolHandler();
        Integer acceptCount = serverConf.getAcceptCount();
        Integer maxThreads = serverConf.getMaxThreads();
        Integer minSpareThreads = serverConf.getMinSpareThreads();
        Integer maxConnections = serverConf.getMaxConnections();
        Integer connectionTimeout = serverConf.getConnectionTimeout();
        Integer maxHttpHeaderSize = serverConf.getMaxHttpHeaderSize();
        Integer maxSavePostSize = serverConf.getMaxSavePostSize();
        if(acceptCount!=null)handler.setAcceptCount(acceptCount);//最大等待队列长度
        if(maxThreads!=null)handler.setMaxThreads(maxThreads);// 线程池的最大线程数
        if(minSpareThreads!=null)handler.setMinSpareThreads(minSpareThreads);// 最小线程数
        if(maxConnections!=null) handler.setMaxConnections(maxConnections);//最大链接数
        if(connectionTimeout!=null) handler.setConnectionTimeout(connectionTimeout);// 超时时间
        if(maxHttpHeaderSize!=null) handler.setMaxHttpHeaderSize(maxHttpHeaderSize);//请求头最大长度kb
        if(maxSavePostSize!=null)handler.setMaxSavePostSize(maxSavePostSize);//请请求体最大长度kb
    }

    /**
     * 启动Tomcat服务
     */
    private void start(){
        try {
            connector=tomcat.getConnector();
            connectorConf();
            tomcat.init();
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    /**
     * 配置Tomcat
     */
    private void tomcatConf(){
        String baseDir = serverConf.getBaseDir();
        tomcat.setPort(serverConf.getPort());
        tomcat.setBaseDir(baseDir);
        File webapps = new File(baseDir + "webapps"+File.separator+"ROOT");
        if(!webapps.exists())webapps.mkdirs();
        tomcat.getHost().setAutoDeploy(serverConf.isAutoDeploy());
        if (Assert.isNotNull(serverConf.getClosePort())) {
            tomcat.getServer().setPort(serverConf.getClosePort());
        }
        if (Assert.isNotNull(serverConf.getShutdown())) {
            tomcat.getServer().setShutdown(serverConf.getShutdown());
        }
    }

    /**
     * 配置上下文
     */
    private void contextConf(){
        context.setSessionTimeout(serverConf.getSessionTimeout());
        context.setPath(serverConf.getContextPath());
        context.setReloadable(serverConf.isReloadable());
        context.setLoader(new WebappLoader(Thread.currentThread().getContextClassLoader()));
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
//        context.addLifecycleListener(new ContextConfig());
    }

    /**
     * 初始化所有ServletContainerInitializer
     */
    private void init(){
        ServletContainerInitializerController initializerController = ServletContainerInitializerController.create(applicationContext);
        List<ServletContainerInitializerController.ServletContainerInitializerAndHandlesTypes> servletContainerInitializerAndHandlesTypes
                = initializerController.getServletContainerInitializerAndHandlesTypes();
        for (ServletContainerInitializerController.ServletContainerInitializerAndHandlesTypes initializerAndHandlesType : servletContainerInitializerAndHandlesTypes) {
            context.addServletContainerInitializer(initializerAndHandlesType.getServletContainerInitializer(),
                    initializerAndHandlesType.getHandlesTypes());
        }
    }

    /**
     * Tomcat正常关闭时执行的销毁工作
     */
    private void doShutDownWork() {
        Runtime.getRuntime().addShutdownHook(new Thread(applicationContext::destroy));
    }

}
