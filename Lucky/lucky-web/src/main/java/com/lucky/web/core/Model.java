package com.lucky.web.core;

import com.lucky.framework.serializable.JSONSerializationScheme;
import com.lucky.framework.serializable.XMLSerializationScheme;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.conversion.JavaConversion;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.error.ErrorPage;
import com.lucky.web.exception.RealPathNotFoundException;
import com.lucky.web.mapping.ExceptionMappingCollection;
import com.lucky.web.mapping.UrlMappingCollection;
import com.lucky.web.webfile.MultipartFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/18 11:07
 */
public class Model {

    private static final Logger log = LogManager.getLogger(Model.class);
    private static final JSONSerializationScheme jsonSerialization= WebConfig.getWebConfig().getJsonSerializationScheme();
    private static final XMLSerializationScheme xmlSerialization=WebConfig.getWebConfig().getXmlSerializationScheme();

    /** 解码方式*/
    private String encod = "ISO-8859-1";
    /** 当前请求的URI*/
    private String uri;
    /*** Request对象*/
    private HttpServletRequest request;
    /** * Response对象*/
    private HttpServletResponse response;
    /*** ServletConfig对象*/
    private ServletConfig servletConfig;
    /*** url请求的方法*/
    private RequestMethod requestMethod;
    /*** 页面参数集合Map<String,String[]>*/
    private Map<String, String[]> parameterMap;
    /*** MultipartFile类型文件参数集合*/
    private Map<String, MultipartFile[]> multipartFileMap;
    /*** File类型的文件参数集合*/
    private Map<String, File[]> uploadFileMap;
    /*** Rest风格的参数集合Map<String,String>*/
    private Map<String, String> restMap;
    /** Servlet输出流*/
    private ServletOutputStream outputStream;
    /** URL映射集*/
    private UrlMappingCollection urlMappingCollection;
    /** 异常处理映射集*/
    private ExceptionMappingCollection exceptionMappingCollection;

    /**
     * Model构造器
     *
     * @param request       Request对象
     * @param response      Response对象
     * @param requestMethod url请求的方法
     * @param encod         解码方式
     * @throws IOException
     */
    public Model(HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig, RequestMethod requestMethod, String encod) {
        this.servletConfig = servletConfig;
        this.encod = encod;
        this.requestMethod = requestMethod;
        init(request, response);
    }

    public Model(){
        WebContext currentContext = WebContext.getCurrentContext();
        init(currentContext.getRequest(),currentContext.getResponse());
        servletConfig=currentContext.getServletConfig();
        requestMethod=currentContext.getRequestMethod();
    }

    public Model(HttpServletRequest request, HttpServletResponse response){
        init(request,response);
    }

    public void init(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
        try {
            this.request.setCharacterEncoding("utf8");
            this.response.setCharacterEncoding("utf8");
            this.response.setHeader("Content-Type", "text/html;charset=utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.uri=request.getRequestURI();
        this.parameterMap = getRequestParameterMap();
        this.multipartFileMap = new HashMap<>();
        this.restMap = new HashMap<>();
        this.uploadFileMap = new HashMap<>();
    }

    /***
     * 获取Response对象的OutputStream
     * @return
     * @throws IOException
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if(outputStream==null){
            outputStream=getResponse().getOutputStream();
        }
        return outputStream;
    }


    public UrlMappingCollection getUrlMappingCollection() {
        return urlMappingCollection;
    }

    public void setUrlMappingCollection(UrlMappingCollection urlMappingCollection) {
        this.urlMappingCollection = urlMappingCollection;
    }

    public ExceptionMappingCollection getExceptionMappingCollection() {
        return exceptionMappingCollection;
    }

    public void setExceptionMappingCollection(ExceptionMappingCollection exceptionMappingCollection) {
        this.exceptionMappingCollection = exceptionMappingCollection;
    }

    /**
     * 判断文件参数中是否包含key
     * @param key key
     * @return
     */
    public boolean uploadFileMapContainsKey(String key) {
        return uploadFileMap.containsKey(key);
    }

    /**
     * 获取某个key对应的所有的文件参数
     * @param key key
     * @return
     */
    public File[] getUploadFileArray(String key) {
        return uploadFileMap.get(key);
    }

    /**
     * 判断MultipartFil文件参数中是否包含key
     * @param key key
     * @return
     */
    public boolean multipartFileMapContainsKey(String key) {
        return multipartFileMap.containsKey(key);
    }

    /**
     * 得到所有文件参数key-File所组成的Map
     * @return
     */
    public Map<String, File[]> getUploadFileMap() {
        return uploadFileMap;
    }

    public void addUploadFile(String key, File[] uploadFiles) {
        uploadFileMap.put(key, uploadFiles);
    }

    public void setUploadFileMap(Map<String, File[]> uploadFileMap) {
        this.uploadFileMap = uploadFileMap;
    }

    public MultipartFile[] getMultipartFileArray(String key) {
        return multipartFileMap.get(key);
    }

    public Map<String, MultipartFile[]> getMultipartFileMap() {
        return multipartFileMap;
    }

    public void addMultipartFile(String key, MultipartFile[] multipartFiles) {
        this.multipartFileMap.put(key, multipartFiles);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getEncod() {
        return encod;
    }

    public Map<String, String> getRestMap() {
        return restMap;
    }

    /**
     * 添加一个文件参数
     * @param multipartFileMap
     */
    public void setMultipartFileMap(Map<String, MultipartFile[]> multipartFileMap) {
        this.multipartFileMap = multipartFileMap;
    }

    /**
     * 设置RestParamMap
     * @param restMap
     */
    public void setRestParams(Map<String, String> restMap) {
        this.restMap = restMap;
    }

    public void addParameter(String key, String[] values) {
        parameterMap.put(key, values);
    }

    /**
     * 得到所有页面参数集合RequestParameterMap
     * @return parameterMap--><Map<String,String[]>>
     */
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public int getParameterSize() {
        return parameterMap.size();
    }

    public String getDefaultParameterValue(){
        String[] values = new ArrayList<>(parameterMap.values()).get(0);
        return values[values.length-1];
    }

    /**
     * 得到当前请求的请求类型
     * @return
     */
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    /**
     * 设置当前请求的请求类型
     * @param requestMethod
     */
    protected void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * 将文本信息写入Cookie
     * @param name   "K"
     * @param value  "V"
     * @param maxAge 内容的最长保存时间
     */
    public void setCookieContent(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        getResponse().addCookie(cookie);
    }

    /**
     * 根据"name"获取Cookit中的文本信息,并转化为指定的编码格式
     * @param name     NAME
     * @param encoding 编码方式
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getCookieContent(String name, String encoding) throws UnsupportedEncodingException {
        String info = null;
        Cookie[] cookies = getRequest().getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                info = cookie.getValue();
                info = URLDecoder.decode(info, encoding);
            }
        }
        return info;
    }

    /**
     * 根据"name"获取Cookit中的文本信息(UTF-8)
     *
     * @param name
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getCookieContent(String name) throws UnsupportedEncodingException {
        String info = null;
        Cookie[] cookies = getRequest().getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                info = cookie.getValue();
                info = URLDecoder.decode(info, "UTF-8");
            }
        }
        return info;
    }

    /**
     * 向request域对象中存值
     * @param name
     * @param value
     */
    public void addAttribute(String name, Object value) {
        getRequest().setAttribute(name, value);
    }

    /**
     * 向request域中取Object类型值
     * @param name
     * @return
     */
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    /**
     * 得到String类型的页面参数
     * @param name
     * @return
     */
    public String getParameter(String name) {
        String[] array = getParams(name);
        return array[array.length-1];
    }

    /**
     * 向session域中存值
     *
     * @param name
     * @param object
     */
    public void setSessionAttribute(String name, Object object) {
        getSession().setAttribute(name, object);
    }

    /**
     * 向session域中取值
     *
     * @param name
     * @return
     */
    public Object getSessionAttribute(String name) {
        return request.getSession().getAttribute(name);
    }

    /**
     * 向application域中存值
     * @param name
     * @param object
     */
    public void setServletContext(String name, Object object) {
        getServletContext().setAttribute(name, object);
    }

    /**
     * 向application域中取值
     * @param name
     * @return
     */
    public Object getServletContextAttribute(String name) {
        return getServletContext().getAttribute(name);
    }

    /**
     * 使用response对象的Writer方法将对象模型写出为JSON格式数据
     * @param pojo 模型数据
     */
    public void writerJson(Object pojo) throws IOException {
        getResponse().setContentType("application/json");
        writer(jsonSerialization.serialization(pojo));
    }

    public Object fromJson(Type type, String jsonStr) throws Exception {
        return jsonSerialization.deserialization(type, jsonStr);
    }

    /**
     * 使用response对象的Writer方法将对象模型写出为XML格式数据
     * @param pojo 模型数据
     */
    public void writerXml(Object pojo) throws IOException {
        getResponse().setContentType("application/xml");
        writer(xmlSerialization.serialization(pojo));
    }

    /**
     * 使用response对象的Writer方法将对象模型写出为JS格式数据
     * @param pojo 模型数据
     */
    public void writerJs(Object pojo){
        getResponse().setContentType("application/x-javascript");
        StringBuilder js=new StringBuilder("<script>").append(pojo).append("</script>");
        writer(js.toString());
    }

    public Object fromXml(Type type, String jsonStr) throws Exception {
        return xmlSerialization.deserialization(type, jsonStr);
    }

    /**
     * 使用response对象的Writer方法写出数据
     * @param info
     */
    public void writer(Object info) {
        try {
            getOutputStream().write(info.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            e500(e);
        }
    }

    /**
     * 使用response对象的Writer方法将Reader中的数据返回
     * @param in
     * @throws IOException
     */
    public void writerReader(Reader in) throws IOException {
        StringWriter sw=new StringWriter();
        IOUtils.copy(in,sw);
        writer(sw.toString());
    }


    /**
     * 得到request对象
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * 得到Appliction对象
     *
     * @return
     */
    public ServletContext getServletContext() {
        return getRequest().getServletContext();
    }

    /**
     * 得到ServletConfig对象
     *
     * @return
     */
    public ServletConfig getServletConfig() {
        return this.servletConfig;
    }

    /**
     * 得到response对象
     *
     * @return
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * 得到session对象
     *
     * @return
     */
    public HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * 得到RequestParameterMap
     *
     * @return parameterMap--><Map<String,String[]>>
     */
    private Map<String, String[]> getRequestParameterMap() {
        HttpServletRequest request = getRequest();
        Map<String, String[]> res = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] mapStr = entry.getValue();
            String[] mapStr_cpoy = new String[mapStr.length];
            for (int i = 0; i < mapStr.length; i++) {
                try {
                    String characterEncoding = request.getCharacterEncoding();
                    mapStr_cpoy[i] = new String(mapStr[i].getBytes(encod), characterEncoding);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            res.put(entry.getKey(), mapStr_cpoy);
        }
        return res;
    }

    /**
     * 判断parameterMap中是否存在值为paramName的Key
     *
     * @param paramName
     * @return
     */
    public boolean parameterMapContainsKey(String paramName) {
        return parameterMap.containsKey(paramName);
    }

    public boolean restMapContainsKey(String paramName) {
        return restMap.containsKey(paramName);
    }

    /**
     * 将String类型的数组转为其他类型的数组String[]->{Integer[],Double[]....}
     *
     * @param strArr
     * @param changTypeClass
     * @return T[]
     */
    public <T> T[] strArrayChange(String[] strArr, Class<T> changTypeClass) {
        return (T[]) JavaConversion.strArrToBasicArr(strArr, changTypeClass);
    }

    /**
     * 得到parameterMap中key对应String[]
     *
     * @param key 键
     * @return
     */
    public String[] getParams(String key) {
        return parameterMap.get(key);
    }

    /**
     * 得到parameterMap中key对应String[]转型后的T[]
     *
     * @param key  键
     * @param clzz 目标类型T的Class
     * @return
     */
    public <T> T[] getParams(String key, Class<T> clzz) {
        return (T[]) JavaConversion.strArrToBasicArr(parameterMap.get(key), clzz);
    }

    /**
     * 得到RestParamMap中key对应Value
     *
     * @param key
     * @return
     */
    public String getRestParam(String key) {
        return restMap.get(key);
    }

    /**
     * 得到RestParamMap中key对应的String转型后的T
     * @param key  键
     * @param clzz 目标类型T的Class
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getRestParam(String key, Class<T> clzz) {
        return (T) JavaConversion.strToBasic(restMap.get(key), clzz);
    }

    /**
     * 转发
     * @param address
     * @throws ServletException
     * @throws IOException
     */
    public void forward(String address) {
        try {
            getRequest().getRequestDispatcher(address).forward(getRequest(), getResponse());
        } catch (ServletException |IOException e) {
            throw new RuntimeException("转发失败，请检查转发地址！["+address+"]...",e);
        }
    }

    /**
     * 重定向
     * @param address
     * @throws IOException
     */
    public void redirect(String address) {
        try {
            getResponse().sendRedirect(address);
        } catch (IOException e) {
            throw new RuntimeException("重定向失败，请检查重定向地址！["+address+"]...",e);
        }
    }

    /**
     * 获取访问者IP地址
     * @return
     */
    public String getIpAddr() {
        String ip = null;
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = getRequest().getHeader("X-Forwarded-For");
        String unknown = "unknown";
        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = getRequest().getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = getRequest().getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = getRequest().getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = getRequest().getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            ip = getRequest().getRemoteAddr();
        }
        return ip;
    }

    /**
     * 向浏览器返回详细的404错误日志
     * @param e
     */
    public void e404(Throwable e) {
        error(e,"404");
    }

    /**
     * 向浏览器返回详细的404错误日志
     * @param e
     */
    public void e403(Throwable e) {
        error(e,"403");
    }

    /**
     * 向浏览器返回详细的404错误日志
     * @param e
     */
    public void e500(Throwable e) {
        error(e,"500");
    }

    /**
     * 向浏览器返回详细错误日志
     * @param e
     * @param errorCode
     */
    public void error(Throwable e,String errorCode) {
        StringWriter buffer=new StringWriter();
        e.printStackTrace(new PrintWriter(buffer));
        e.printStackTrace();
        log.error(buffer.toString());
        error(errorCode,buffer.toString(),e.toString());
    }

    public void error(String errorCode,String Message,String Description) {
        try {
            //"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36"
            String userAgent = getRequest().getHeader("User-Agent");
            if(userAgent.startsWith("Mozilla/")){
                getResponse().setContentType("text/html");
                Message=Message.replaceAll("\\r\\n", "<br/>").replaceAll("\\t", "&emsp;&emsp;");
                Message=Message.replaceAll("\\n","<br/>");
                writer(ErrorPage.exception(errorCode, Message, Description));
            }else{
                writerJson(new ExceptionMessage(errorCode,Description,Message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回项目发布后file文件(夹)的绝对路径
     * @param file
     * @return
     */
    public String getRealPath(String file) {
        try {
            return getServletContext().getRealPath(file);
        }catch (Exception e){
            throw new RealPathNotFoundException(e);
        }
    }

    public boolean docBaseIsExist(){
        return Assert.isNull(getServletContext().getRealPath(""));
    }

    public File getRealFile(String uri) {
        return new File(getServletContext().getRealPath(uri));
    }

}

class ExceptionMessage{

    /**错误时间*/
    private Date time;
    /** 错误代码*/
    private String code;
    /** 错误异常*/
    private String exception;
    /** 堆栈信息*/
    private String stack;

    public ExceptionMessage(String code, String exception, String stack) {
        this.time=new Date();
        this.code = code;
        this.exception = exception;
        this.stack = stack;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }


}
