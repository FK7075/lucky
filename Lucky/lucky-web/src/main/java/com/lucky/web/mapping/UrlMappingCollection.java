package com.lucky.web.mapping;

import com.lucky.framework.uitls.file.Resources;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.MethodUtils;
import com.lucky.web.annotation.CloseRun;
import com.lucky.web.annotation.InitRun;
import com.lucky.web.core.Model;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.exception.CloseRunException;
import com.lucky.web.exception.InitRunException;
import com.lucky.web.exception.RepeatUrlMappingException;
import com.lucky.web.webfile.WebFileUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 10:03
 */
public class UrlMappingCollection {

    private List<UrlMapping> list;
    private List<UrlMapping> runList;

    public UrlMappingCollection(){
        list=new ArrayList<>(10);
        runList=new ArrayList<>(10);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(UrlMapping map) {
        for (UrlMapping urlMapping : list) {
            if(urlMapping.isEquals(map)){
                return true;
            }
        }
        return false;
    }

    public Iterator<UrlMapping> iterator() {
        return list.iterator();
    }

    public Iterator<UrlMapping> runIterator() {
        return runList.iterator();
    }

    public boolean add(UrlMapping urlMapping) {
        if(contains(urlMapping)){
            throw new RepeatUrlMappingException(urlMapping);
        }
        list.add(urlMapping);
        return true;
    }


    public boolean addRun(Object controller, Method runMethod){
        runList.add(new UrlMapping(null,null,null,
                controller,runMethod,null,null,null,null));
        return true;
    }

    public boolean addRun(UrlMapping urlMapping){
        runList.add(urlMapping);
        return true;
    }

    public void clear() {
        list.clear();
    }

    /**
     * 执行初始化方法「被@InitRun标注的Controller方法」
     */
    public void initRun(){
        runList.stream()
            .filter(m -> AnnotationUtils.isExist(m.getMapping(), InitRun.class))
            .sorted(Comparator.comparing(m->AnnotationUtils.get(m.getMapping(),InitRun.class).value()))
            .forEach((m)->{
                if(m.getParameters().length!=0){
                    throw new InitRunException(m.getMapping());
                }
                MethodUtils.invoke(m.getObject(),m.getMapping());
            });
    }

    /**
     * 执行关闭方法「被@CloseRun标注的Controller方法」
     */
    public void closeRun(){
        runList.stream()
            .filter(m -> AnnotationUtils.isExist(m.getMapping(), CloseRun.class))
            .sorted(Comparator.comparing(m->AnnotationUtils.get(m.getMapping(),CloseRun.class).value()))
            .forEach((m)->{
                if(m.getParameters().length!=0){
                    throw new CloseRunException(m.getMapping());
                }
                MethodUtils.invoke(m.getObject(),m.getMapping());
            });
    }

    public UrlMapping getMapping(Model model) throws IOException {
        UrlMapping urlMapping =getByUrl(model);
        if(urlMapping ==null){
            if("/".equals(model.getUri())){
                WebFileUtils.preview(model, Resources.getInputStream("/lucky-web/LUCKY.html"),"LUCKY.html");
                return null;
            }
            model.error("404", "找不与请求相匹配的映射资,请检查您的URL是否正确！","不正确的url："+model.getUri());
            return null;
        }
        String currIp=model.getIpAddr();
        if("0:0:0:0:0:0:0:1".equals(currIp)){
            currIp="127.0.0.1";
        }
        if (!urlMapping.ipExistsInRange(currIp) || !urlMapping.ipISCorrect(currIp)) {
            model.error("403", "该ip地址没有被注册，服务器拒绝响应！", "不合法的请求ip：" + currIp);
            return null;
        }
        RequestMethod method=model.getRequestMethod();
        if(!urlMapping.methodIsEquals(method)){
            model.error("403","您的请求类型["+method+"] , 当前方法并不支持！","不合法的请求类型["+method+"]!");
            return null;
        }
        return urlMapping;
    }

    private UrlMapping getByUrl(Model model){
        String url=model.getUri();
        for (UrlMapping urlMapping : list) {
            if(urlMapping.urlIsEquals(url)){
                return urlMapping;
            }
            if(restUrlVerify(model, urlMapping.getUrl())){
                return urlMapping;
            }
        }
        return null;
    }
    
    private boolean restUrlVerify(Model model,String confUrl){
        return false;
    }

    public boolean merge(UrlMappingCollection collection){
        Iterator<UrlMapping> iterator = collection.iterator();
        while (iterator.hasNext()){
            add(iterator.next());
        }
        return true;
    }
}
