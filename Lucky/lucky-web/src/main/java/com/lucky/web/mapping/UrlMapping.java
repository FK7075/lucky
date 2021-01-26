package com.lucky.web.mapping;

import com.lucky.utils.base.Assert;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.MethodUtils;
import com.lucky.web.annotation.Download;
import com.lucky.web.authority.AuthorityCheck;
import com.lucky.web.core.Model;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.enums.Rest;
import com.lucky.web.httpclient.HttpClientCall;
import com.lucky.web.utils.IpUtil;
import com.lucky.web.webfile.WebFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

/**
 * 一个具体的URL映射
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 9:53
 */
public class UrlMapping extends Mapping{

    /** 组件的唯一ID*/
    private String iocId;
    /** 组件的类型*/
    private String iocType;
    /** URL*/
    private String url;
    /** 当前的请求类型*/
    private RequestMethod[] methods;
    /** 该请求支持的ip地址*/
    private Set<String> ips;
    /** 该请求支持的ip段范围*/
    private String[] ipSection;
    /** 权限校验*/
    private AuthorityCheck authorityCheck;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestMethod[] getMethods() {
        return methods;
    }

    public void setMethods(RequestMethod[] methods) {
        this.methods = methods;
    }

    public Set<String> getIps() {
        return ips;
    }

    public void setIps(Set<String> ips) {
        this.ips = ips;
    }

    public String[] getIpSection() {
        return ipSection;
    }

    public void setIpSection(String[] ipSection) {
        this.ipSection = ipSection;
    }

    public String getIocId() {
        return iocId;
    }

    public void setIocId(String iocId) {
        this.iocId = iocId;
    }

    public String getIocType() {
        return iocType;
    }

    public void setIocType(String iocType) {
        this.iocType = iocType;
    }

    public UrlMapping(){}

    public UrlMapping(String url, String iocId, String iocType, Object controller,
                      Method mapping, RequestMethod[] methods,
                      Rest rest, Set<String>ips,
                      String[] ipSection) {
        this.url = url;
        this.iocId=iocId;
        this.iocType=iocType;
        this.rest = rest;
        this.object = controller;
        this.mapping = mapping;
        this.methods = methods;
        this.parameters=mapping.getParameters();
        this.ips=ips;
        this.ipSection=ipSection;
        this.authorityCheck=new AuthorityCheck(controller.getClass(),mapping);
    }

    /**
     * 权限校验
     * @return
     */
    public boolean permissionCheck(){
        return authorityCheck.check();
    }

    /**
     * 判断当前Mapping是否等价于本身
     * @param currUrlMapping 当前Mapping
     * @return
     */
    public boolean isEquals(UrlMapping currUrlMapping){
        if(Assert.isNull(currUrlMapping)){
            return false;
        }
        //URL校验
        boolean urlEq=urlIsEquals(currUrlMapping.getUrl());
        //支持的请求类型校验
        RequestMethod[] currMethods = currUrlMapping.getMethods();
        boolean methodEq=methodIsEquals(currMethods);

        return urlEq&&methodEq;
    }

    /**
     * [添加时]请求类型校验
     * @param currMethods 待校验的请求类型
     * @return
     */
    public boolean methodIsEquals(RequestMethod[] currMethods){
        for (RequestMethod currMethod : currMethods) {
            if(methodIsEquals(currMethod)){
                return true;
            }
        }
        return false;
    }

    /**
     * [请求时]请求类型校验
     * @param method 待校验的请求类型
     * @return
     */
    public boolean methodIsEquals(RequestMethod method){
        for (RequestMethod requestMethod : getMethods()) {
            if(method==requestMethod){
                return true;
            }
        }
        return false;
    }

    /**
     * URL校验
     * @param url 待验证的URL
     * @return
     */
    public boolean urlIsEquals(String url){
        return simpleUrlIsEquals(url)||addingRestUrlIsEquals(url);
    }

    /**
     * 简单URL校验，字符串的全匹配
     * @param url
     * @return
     */
    public boolean simpleUrlIsEquals(String url){
        return getUrl().equals(url);
    }

//    /**
//     * [查找时] REST URL校验，当有一个URL请求过来时的校验
//     * 校验成功后会将Rest参数设置到Model对象中
//     * @param model 当前请求的Model对象
//     * @param requestUrl 待验证的请求 URL
//     * @return
//     */
//    public boolean findingRestUelIsEquals(Model model,String requestUrl){
//        String[] thisURLArray = getUrl().split("/");
//        String[] requestURLArray = requestUrl.split("/");
//        int thisURLArrayLength = thisURLArray.length;
//        int requestURLArrayLength = requestURLArray.length;
//
//        //URL长度校验
//        if("**".equals(thisURLArray[thisURLArrayLength-1])){
//            //以通配符/**结尾的URL映射，要求请求的URL的元素长度必须不小于映射的元素长度
//            if(thisURLArrayLength<requestURLArrayLength){
//                return false;
//            }
//        }else if(thisURLArray[thisURLArrayLength-1].endsWith("**")){
//
//        }else{
//            if(thisURLArrayLength!=requestURLArrayLength){
//                return false;
//            }
//        }
//
//        for (int i = 0,j=thisURLArray.length; i < j; i++) {
//            if(!findingRestURLElementTest(requestURLArray[i],thisURLArray[i])){
//                return false;
//            }
//        }
////        Map<String,String> restMap=new HashMap<>();
////        String key;
////        for (int i = 0,j=thisURLArray.length; i < j; i++) {
////            if(isRestElement(thisURLArray[i])){
////                key=thisURLArray[i].startsWith("#{")?thisURLArray[i].substring(2):thisURLArray[i];
////                key=key.startsWith("{")?key.substring(1):key;
////                key=key.endsWith("}")?key.substring(0,key.length()-1):key;
////                restMap.put(key,requestURLArray[i]);
////            }
////        }
////        model.setRestParams(restMap);
//        return true;
//    }

//    /**
//     * [查找时] 判断两个REST URL元素是否等价
//     * @param urlElement 待验证的URL元素
//     * @param testElement
//     * @return
//     */
//    private boolean findingRestURLElementTest(String urlElement,String testElement){
//        boolean isRestElement=isRestElement(testElement);
//
//        //testElement为REST URL元素或者为通配符*、**、{xx}**、#{xx}**
//        if(isRestElement||"*".equals(testElement)||"**".equals(testElement)||isRestWildcardElement(testElement)==1){
//            return true;
//        }
//
//        if(testElement.startsWith("*")){
//            return urlElement.endsWith(testElement.substring(1));
//        }
//        if(testElement.endsWith("*")){
//            return urlElement.startsWith(testElement.substring(0,testElement.length()-1));
//        }
//
//        return urlElement.equals(testElement);
//    }


    /**
     * [添加时] REST URL校验，向集合中添加Mapping时的REST URL校验
     * 以下这些REST URL会被视为等价
     * user/#{uid}/{jack}/ <=>
     * user/{uid}/{jack}/ <=>
     * user/{lucy}/#{tomcat}/
     * @param url 待验证的REST URL
     * @return
     */
    public boolean addingRestUrlIsEquals(String url) {
        String[] thisURLArray = getUrl().split("/");
        String[] verifiedURLArray = url.split("/");
        if(thisURLArray.length!=verifiedURLArray.length){
            return false;
        }
        for (int i = 0,j=thisURLArray.length; i < j; i++) {
            if(!addingRestURLElementTest(thisURLArray[i],verifiedURLArray[i])){
                return false;
            }
        }
        return true;
    }

    /**
     * [添加时]判断两个REST URL元素是否等价
     * @param thisElement 元素1
     * @param verifiedURLElement 元素2
     * @return
     */
    private boolean addingRestURLElementTest(String thisElement,String verifiedURLElement){
        boolean thisIsRest=isRestElement(thisElement);
        boolean verifiedIsRest=isRestElement(verifiedURLElement);
        //一个是REST元素一个不是REST元素
        if((!verifiedIsRest&&thisIsRest)||(verifiedIsRest&&!thisIsRest)){
            return false;
        }
        //两个都不是REST元素
        if(!thisIsRest&&!verifiedIsRest){
            int thisCode = isRestWildcardElement(thisElement);
            int verifiedCode = isRestWildcardElement(verifiedURLElement);
            if(thisCode!=verifiedCode){
                return false;
            }
            if (thisCode==1){
                return true;
            }
            return thisElement.equals(verifiedURLElement);
        }
        //两个都是REST元素
        return true;
    }

    /**
     * 判断一个URL元素是否为Rest通配符元素
     * -1
     * 1.{abc}**
     * @param urlElement
     * @return
     */
    private int isRestWildcardElement(String urlElement){
        if(urlElement.endsWith("**")){
            return isRestElement(urlElement.substring(0,urlElement.length()-2))?1:-1;
        }
        return -1;
    }

    /**
     * 判断一个URL元素是否为REST URL元素
     * @param urlElement
     * @return
     */
    private boolean isRestElement(String urlElement){
        return urlElement.startsWith("{")&&urlElement.endsWith("}");
    }

    /**
     * 执行Controller方法
     * @return 方法执行后的返回值
     */
    public Object invoke(Model model) throws IOException, URISyntaxException {
        Object result = MethodUtils.invoke(object, mapping, runParams);
        if(AnnotationUtils.isExist(mapping, Download.class)){
            download(model);
        }
        return result;
    }

    public boolean ipExistsInRange(String ip) {
        if(ipSection==null||ipSection.length==0) {
            return true;
        }
        for(String hfip:ipSection) {
            if(hfip.startsWith("!")) {//判断该ip是否属于非法IP段
                if(IpUtil.ipExistsInRange(ip,hfip.substring(1))) {
                    return false;
                }
            }else if(IpUtil.ipExistsInRange(ip,hfip)){//判断该ip是否属性合法ip段
                return true;
            }
        }
        return false;//非非法ip段也非合法ip段，即为未注册ip，不给予通过
    }

    public boolean ipISCorrect(String currip) {
        if(ips.isEmpty()) {
            return true;
        }
        if("localhost".equals(currip)) {
            currip="127.0.0.1";
        }
        for(String ip:ips) {
            if(ip.startsWith("!")) {//判断该ip是否属于非法IP
                if(currip.equals(ip.substring(1))) {
                    return false;
                }
            }else if(currip.equals(ip)) {//判断该ip是否属性合法ip
                return true;
            }
        }
        return false;//非非法ip也非合法ip，即为未注册ip，不给予通过
    }

    /**
     * 注解版文件下载操作@Download
     * @param model  Model对象
     * @throws IOException
     */
    public void download(Model model) throws IOException, URISyntaxException {
        Download dl = mapping.getAnnotation(Download.class);
        InputStream fis = null;
        String downName = null;
        String path = "";
        if (!Assert.isBlankString(dl.path())) {
            path = dl.path();
        } else if (!Assert.isBlankString(dl.docPath())) {
            path = model.getRealPath("") + dl.docPath();
        } else if (!Assert.isBlankString(dl.url())) {
            String url = dl.url();
            byte[] buffer = HttpClientCall.getCallByte(url, new HashMap<>());
            String fileName = model.getResponse().getHeader("Content-Disposition");
            if (fileName == null) {
                fileName = "lucky_" + BaseUtils.getRandomNumber() + url.substring(url.lastIndexOf("."));
            } else {
                fileName.replaceAll("attachment;filename=", "").trim();
            }
            WebFileUtils.download(model.getResponse(), buffer, fileName);
            return;
        } else {
            String fileName = dl.name();
            String filePath = dl.library();
            String file;
            if (model.parameterMapContainsKey(fileName)) {
                file = model.getParameter(fileName);// 客户端传递的需要下载的文件名
            } else if (model.restMapContainsKey(fileName)) {
                file = model.getRestParam(fileName);
            } else {
                model.error("403","找不到文件下载接口的必要参数 \""+fileName + "\"","缺少接口参数..");
                return;
            }
            if (filePath.startsWith("abs:")) {
                path = filePath.substring(4) + file;//绝对路径写法
            } else if (filePath.startsWith("http:")) {//暴露一个网络上的文件库
                String url = filePath + file;
                byte[] buffer = HttpClientCall.getCallByte(url, new HashMap<>());
                WebFileUtils.download(model.getResponse(), buffer, file);
                return;
            } else {
                path = model.getRealPath(filePath) + file; // 默认认为文件在当前项目的docBase目录
            }
        }
        if (fis == null) {
            File f = new File(path);
            if (!f.exists()){
                model.error("404","在服务器上没有发现您想要下载的资源"+f.getName(),"没有对应的资源。");
                return;
            }

            fis = new FileInputStream(f);
            downName = f.getName();
        }
        WebFileUtils.download(model.getResponse(), fis, downName);
    }

    private class UrlMappingComparator implements Comparator<UrlMapping> {

        @Override
        public int compare(UrlMapping o1, UrlMapping o2) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return String.format("{%s%s} => %s#%s", Arrays.toString(methods),url,mapping.getDeclaringClass().getName(),mapping.getName());
    }
}
