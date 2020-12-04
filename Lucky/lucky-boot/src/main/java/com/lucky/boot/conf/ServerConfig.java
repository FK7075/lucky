package com.lucky.boot.conf;

import com.lucky.boot.annotation.LuckyFilter;
import com.lucky.boot.annotation.LuckyServlet;
import com.lucky.boot.web.FilterMapping;
import com.lucky.boot.web.ServletMapping;
import com.lucky.framework.ApplicationContext;
import com.lucky.framework.confanalysis.LuckyConfig;
import com.lucky.framework.uitls.base.BaseUtils;
import com.lucky.web.servlet.LuckyDispatcherServlet;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.*;

/**
 * 服务器的配置信息
 * @author fk
 * @version 1.0
 * @date 2020/12/3 0003 11:51
 */
public class ServerConfig extends LuckyConfig {

    private static ServerConfig serverConfig;
    /**启动端口*/
    private int port;
    /**项目路径*/
    private String contextPath;
    /**Session过期时间*/
    private int sessionTimeout;
    /**webapps目录中增加新的目录、war文件、修改WEB-INF/web.xml，autoDeploy="true"会新建或重新部署应用*/
    private boolean autoDeploy;
    /**替换WEB-INF/lib目录中的jar文件或WEB-INF/classes目录中的class文件时，reloadable="true"会让修改生效*/
    private boolean reloadable;
    /**关机命令的端口*/
    private Integer closePort;
    /**关机命令*/
    private String shutdown;

    private String webapp;

    private String URIEncoding;

    /**
     * 设置一个静态文件的储存库
     *   1.${user.dir}/XXX System.getProperty("user.dir")下的某个文件夹
     *   2.${java.io.tmpdir}/XXX 系统临时文件夹下的某个文件夹
     *   3.XXX 文件夹的绝对路径
     * @param docbase
     */
    private String docBase;

    private String baseDir;

    private List<ServletMapping> servletList;

    private List<FilterMapping> filterList;

    private Set<EventListener> listeners;

    public int getPort() {
        return port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public boolean isAutoDeploy() {
        return autoDeploy;
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public Integer getClosePort() {
        return closePort;
    }

    public String getShutdown() {
        return shutdown;
    }

    public String getWebapp() {
        return webapp;
    }

    public String getURIEncoding() {
        return URIEncoding;
    }

    public String getDocBase() {
        return docBase;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public List<ServletMapping> getServletList() {
        return servletList;
    }

    public List<FilterMapping> getFilterList() {
        return filterList;
    }

    public Set<EventListener> getListeners() {
        return listeners;
    }

    public void setPort(int port) {
        this.port = port;
        serverConfig.setBaseDir("java.io.tmpdir:/tomcat."+serverConfig.getPort()+File.separator);
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setAutoDeploy(boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    public void setClosePort(Integer closePort) {
        this.closePort = closePort;
    }

    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

    public void setURIEncoding(String URIEncoding) {
        this.URIEncoding = URIEncoding;
    }

    public void setDocBase(String docbase) {
        if(docbase.startsWith("user.dir:")){
            docbase=docbase.substring(9).trim();
            docBase=System.getProperty("user.dir")+docbase;
        }else if(docbase.startsWith("java.io.tmpdir:")){
            docbase=docbase.substring(15).trim();
            docbase=docbase.startsWith("/")?docbase.substring(1):docbase;
            docBase=System.getProperty("java.io.tmpdir")+docbase;
        }else{
            docBase=docbase.trim();
        }
    }

    public void setBaseDir(String baseDir) {
        if(baseDir.startsWith("user.dir:")){
            baseDir=baseDir.substring(9).trim();
            this.baseDir=System.getProperty("user.dir")+baseDir;
        }else if(baseDir.startsWith("java.io.tmpdir:")){
            baseDir=baseDir.substring(15).trim();
            baseDir=baseDir.startsWith("/")?baseDir.substring(1):baseDir;
            String s = System.getProperty("java.io.tmpdir");
            s=s.endsWith(File.separator)?s:s+File.separator;
            this.baseDir=s+baseDir;
        }else{
            this.baseDir=baseDir.trim();
        }
    }

    public void setServletList(List<ServletMapping> servletList) {
        this.servletList = servletList;
    }

    public void setFilterList(List<FilterMapping> filterList) {
        this.filterList = filterList;
    }

    public void setListeners(Set<EventListener> listeners) {
        this.listeners = listeners;
    }

    public void addListener(EventListener listener){
        this.listeners.add(listener);
    }

    private ServerConfig(){
        servletList =new ArrayList<>();
        listeners=new HashSet<>();
        filterList =new ArrayList<>();
    }

    private Set<String> getMapping(String className,String[] mapStrArray){
        if(mapStrArray.length!=0){
            return new HashSet<>(Arrays.asList(mapStrArray));
        }
        Set<String> mapping=new HashSet<>(1);
        mapping.add("/"+className);
        return mapping;
    }

    public void addFilter(Filter filter,String...mappings) {
        String filterName=BaseUtils.lowercaseFirstLetter(filter.getClass().getSimpleName());
        FilterMapping filterMapping=new FilterMapping(getMapping(filterName,mappings),filterName,filter);
        filterList.add(filterMapping);
    }

    public void addServlet(HttpServlet servlet,String...mappings) {
        addServlet(servlet,-1,mappings);
    }

    public void addServlet(HttpServlet servlet,int loadOnStartup,String...mappings) {
        String servletName=BaseUtils.lowercaseFirstLetter(servlet.getClass().getSimpleName());
        ServletMapping servletMapping=new ServletMapping(getMapping(servletName,mappings),servletName,servlet,loadOnStartup);
        servletList.add(servletMapping);
    }

    public static ServerConfig defaultServerConfig(){
        if(serverConfig==null){
            serverConfig=new ServerConfig();
            serverConfig.setPort(8080);
            serverConfig.setClosePort(null);
            serverConfig.setShutdown(null);
            serverConfig.setSessionTimeout(30);
            serverConfig.setWebapp("/WebContent");
            serverConfig.addServlet(new LuckyDispatcherServlet(),0,"/");
            serverConfig.setContextPath("");
            serverConfig.setURIEncoding("UTF-8");
            serverConfig.setAutoDeploy(false);
            serverConfig.setReloadable(false);
            serverConfig.setFirst(true);
        }
        return serverConfig;
    }

    public static ServerConfig getServerConfig(){
        ServerConfig serverConfig = defaultServerConfig();
        if(serverConfig.isFirst()){
            YamlParsing.loadServer(serverConfig);
        }
        return serverConfig;
    }

    public void init(ApplicationContext applicationContext) {
        listenerInit(applicationContext);
        servletInit(applicationContext);
        filterInit(applicationContext);
    }

    /**
     * 注解版Listener注册
     */
    private void listenerInit(ApplicationContext applicationContext) {
        List<EventListener> listeners = applicationContext.getBean(EventListener.class);
        listeners.stream().forEach(a->this.listeners.add(a));
    }

    /**
     * 注解版Servlet注册
     */
    private void servletInit(ApplicationContext applicationContext) {
        List<HttpServlet> servlets = applicationContext.getBean(HttpServlet.class);
        ServletMapping servletMap;
        Set<String> smapping;
        for(HttpServlet servlet:servlets) {
            LuckyServlet annServlet=servlet.getClass().getAnnotation(LuckyServlet.class);
            smapping=new HashSet<>(Arrays.asList(annServlet.value()));
            servletMap=new ServletMapping(smapping, BaseUtils.lowercaseFirstLetter(servlet.getClass().getSimpleName()),servlet,annServlet.loadOnStartup());
            servletList.add(servletMap);
        }
    }

    /**
     * 注解版Filter注册
     */
    private void filterInit(ApplicationContext applicationContext) {
        List<Filter> filters = applicationContext.getBean(Filter.class);
        FilterMapping filterMap;
        Set<String> fmapping;
        LuckyFilter annFilter;
        for(Filter filter:filters) {
            annFilter=filter.getClass().getAnnotation(LuckyFilter.class);
            fmapping=new HashSet<>(Arrays.asList(annFilter.value()));
            filterMap=new FilterMapping(fmapping,BaseUtils.lowercaseFirstLetter(filter.getClass().getSimpleName()),filter);
            filterList.add(filterMap);
        }
    }


}
