package com.lucky.boot.conf;

import com.lucky.boot.web.FilterMapping;
import com.lucky.boot.web.ListenerMapping;
import com.lucky.boot.web.ServletMapping;
import com.lucky.framework.ApplicationContext;
import com.lucky.framework.confanalysis.LuckyConfig;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.base.BaseUtils;

import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

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

    private List<ListenerMapping> listenerList;

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

    public List<ListenerMapping> getListenerList() {
        return listenerList;
    }

    public void setListenerList(List<ListenerMapping> listenerList) {
        this.listenerList = listenerList;
    }

    public void addListener(ListenerMapping listenerMapping){
        this.listenerList.add(listenerMapping);
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


    private ServerConfig(){
        servletList =new ArrayList<>();
        listenerList=new ArrayList<>();
        filterList =new ArrayList<>();
    }

    public void addFilter(FilterMapping filterMapping) {
        filterList.add(filterMapping);
    }

    public void addServlet(ServletMapping servletMapping) {
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
        List<EventListener> listeners = (List<EventListener>) applicationContext.getBeanByAnnotation(WebListener.class);
        WebListener listenerAnn;
        for (EventListener listener : listeners) {
            Class<? extends EventListener> listenerClass = listener.getClass();
            listenerAnn=listenerClass.getAnnotation(WebListener.class);
            String listenerName=Assert.isBlankString(listenerAnn.value())?
                    BaseUtils.lowercaseFirstLetter(listenerClass.getSimpleName()):listenerAnn.value();
            listenerList.add(new ListenerMapping(listenerName,listener));
        }
    }

    /**
     * 注解版Servlet注册
     */
    private void servletInit(ApplicationContext applicationContext) {
        List<HttpServlet> servlets = ( List<HttpServlet>)applicationContext.getBeanByAnnotation(WebServlet.class);
        for(HttpServlet servlet:servlets) {
            servletList.add(createServletMapping(servlet));
        }
    }

    private ServletMapping createServletMapping(HttpServlet servlet){
        ServletMapping servletMap=new ServletMapping();
        servletMap.setServlet(servlet);
        Class<? extends HttpServlet> servletClass = servlet.getClass();
        WebServlet annServlet=servletClass.getAnnotation(WebServlet.class);
        String servletClassName = BaseUtils.lowercaseFirstLetter(servletClass.getSimpleName());
        String servletName= Assert.isBlankString(annServlet.name())?servletClassName:annServlet.name();
        servletMap.setName(servletName);
        String[] servletMapping=Assert.isEmptyArray(annServlet.urlPatterns()) ?annServlet.value():annServlet.urlPatterns();
        if(Assert.isEmptyArray(servletMapping)){
            servletMapping=new String[1];
            servletMapping[0]="/"+servletClassName;
        }
        servletMap.setUrlPatterns(servletMapping);
        servletMap.setLoadOnStartup(annServlet.loadOnStartup());
        servletMap.setAsyncSupported(annServlet.asyncSupported());
        servletMap.setSmallIcon(annServlet.smallIcon());
        servletMap.setLargeIcon(annServlet.largeIcon());
        servletMap.setDescription(annServlet.description());
        servletMap.setDisplayName(annServlet.displayName());
        servletMap.setInitParams(annServlet.initParams());
        return servletMap;
    }

    /**
     * 注解版Filter注册
     */
    private void filterInit(ApplicationContext applicationContext) {
        List<Filter> filters = (List<Filter>) applicationContext.getBeanByAnnotation(WebFilter.class);
        for(Filter filter:filters) {
            filterList.add(createFilterMappng(filter));
        }
    }

    private FilterMapping createFilterMappng(Filter filter) {
        FilterMapping fm=new FilterMapping();
        fm.setFilter(filter);
        Class<? extends Filter> filterClass = filter.getClass();
        WebFilter filterAnn=filterClass.getAnnotation(WebFilter.class);
        String filterClassName = BaseUtils.lowercaseFirstLetter(filterClass.getSimpleName());
        String filterName=Assert.isBlankString(filterAnn.filterName())?filterClassName:filterAnn.filterName();
        fm.setName(filterName);

        String[] filterMapping=Assert.isEmptyArray(filterAnn.urlPatterns()) ?filterAnn.value():filterAnn.urlPatterns();
        if(Assert.isEmptyArray(filterMapping)){
            filterMapping=new String[1];
            filterMapping[0]="/"+filterClassName;
        }
        fm.setUrlPatterns(filterMapping);
        fm.setServletNames(filterAnn.servletNames());
        fm.setDispatcherTypes(filterAnn.dispatcherTypes());
        fm.setInitParams(filterAnn.initParams());
        fm.setDescription(filterAnn.description());
        fm.setDisplayName(filterAnn.displayName());
        fm.setSmallIcon(filterAnn.smallIcon());
        fm.setLargeIcon(filterAnn.largeIcon());
        fm.setAsyncSupported(filterAnn.asyncSupported());
        return fm;
    }


}
