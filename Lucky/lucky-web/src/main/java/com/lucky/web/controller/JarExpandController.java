package com.lucky.web.controller;

import com.lucky.framework.ApplicationContext;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.SingletonContainer;
import com.lucky.framework.scan.LuckyURLClassLoader;
import com.lucky.utils.base.Assert;
import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.ControllerAdvice;
import com.lucky.web.annotation.RestController;
import com.lucky.web.exception.AddMappingExpandException;
import com.lucky.web.mapping.DefaultMappingAnalysis;
import com.lucky.web.mapping.ExceptionMappingCollection;
import com.lucky.web.mapping.UrlMappingCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 映射扩展操作的Controller基类
 * @author fk
 * @version 1.0
 * @date 2020/12/2 0002 15:33
 */
public abstract class JarExpandController extends LuckyController{

    private static final Logger log= LoggerFactory.getLogger("c.l.web.controller.JarExpandController");
    private static UrlMappingCollection urlMappingCollection;
    private static ExceptionMappingCollection exceptionMappingCollection;

    protected UrlMappingCollection getUrlMappingCollection(){
        if(urlMappingCollection==null){
            ApplicationContext applicationContext=AutoScanApplicationContext.create();
            urlMappingCollection=(UrlMappingCollection) applicationContext.getBean("lucky_UrlMappingCollection");
        }
        return urlMappingCollection;
    }

    protected ExceptionMappingCollection getExceptionMappingCollection(){
        if(exceptionMappingCollection==null){
            ApplicationContext applicationContext=AutoScanApplicationContext.create();
            exceptionMappingCollection= (ExceptionMappingCollection) applicationContext.getBean("lucky_ExceptionMappingCollection");
        }
        return exceptionMappingCollection;
    }

    /**
     *
     * 添加一个外部的JAR包扩展
     * @param expandName 扩展名
     * @param jarFileUrl jar包的路径「jar:file:/绝对路径!/」「jar:http://网络路径」
     * @throws IOException
     */
    protected void andExpandJar(String expandName,String jarFileUrl,String groupId)throws IOException {
        andExpandJar(new JarExpand(expandName,jarFileUrl,groupId));
    }

    /**
     * 添加一个外部的JAR包扩展
     * @param jarExpand 扩展信息
     * @throws IOException
     */
    protected void andExpandJar(JarExpand jarExpand) throws IOException {
        String expandName=jarExpand.getExpandName();
        String groupId=jarExpand.getGroupId();
        if(Assert.isNull(expandName)){
            throw new AddMappingExpandException("扩展名为NULL！");
        }
        groupId=Assert.isNull(groupId)?"":groupId;
        jarExpand.setGroupId(groupId);
        Set<String> deleteExpandURL = getUrlMappingCollection().getDeleteExpand();
        Set<String> deleteExpandEXP = getExceptionMappingCollection().getDeleteExpand();
        if(deleteExpandURL.contains(expandName)||deleteExpandEXP.contains(expandName)){
            deleteExpandURL.remove(expandName);
            deleteExpandEXP.remove(expandName);
            log.info("扩展集 `{}` 恢复使用！",expandName);
        }else{
            Map<String, UrlMappingCollection> urlMap = getUrlMappingCollection().getExpandMap();
            Map<String, ExceptionMappingCollection> expMap = getExceptionMappingCollection().getExpandMap();
            if(urlMap.containsKey(expandName)||expMap.containsKey(expandName)){
                log.warn("扩展集 `{}` 已存在，无法重复添加！",expandName);
            }else{
                add(jarExpand);
            }
        }
    }

    /**
     * 【逻辑删除】移除一个外部扩展
     * @param expandName 扩展名
     */
    protected void deleteExpandJar(String expandName){
        getUrlMappingCollection().deleteExpand(expandName);
        getExceptionMappingCollection().deleteExpand(expandName);
    }

    /**
     * 【物理删除】移除一个外部扩展
     * @param expandName 扩展名
     */
    protected void removerExpandJar(String expandName){
        getUrlMappingCollection().removerExpand(expandName);
        getExceptionMappingCollection().removerExpand(expandName);
    }


    private final boolean add(JarExpand jarExpand) throws IOException {
        URL[] urls={new URL(jarExpand.getJarPath())};
        URLClassLoader loader = new URLClassLoader(
                urls, Thread.currentThread().getContextClassLoader());
        //创建LuckyURLClassLoader,用于获取目标jar包中的所有IOC组件
        LuckyURLClassLoader luckyURLClassLoader=new LuckyURLClassLoader(urls,loader);
        //获取当前IOC的上下文对象
        AutoScanApplicationContext applicationContext=AutoScanApplicationContext.create();
        //使用当前上下文对象动态的加载由LuckyURLClassLoader扫描得到的jar包中的IOC组件
        SingletonContainer singletonPool = applicationContext.getNewSingletonPool(luckyURLClassLoader.getComponentClass(jarExpand.getGroupId()));
        //构造Lucky的Mapping解析器
        DefaultMappingAnalysis analysis = new DefaultMappingAnalysis();
        List<Module> controllers = singletonPool.getBeanByAnnotation(Controller.class, RestController.class);
        //解析得到所有的请求映射
        UrlMappingCollection urlMappingCollection =analysis.analysis(controllers);
        List<Module> controllerAdvices = singletonPool.getBeanByAnnotation(ControllerAdvice.class);
        //解析得到所有的异常处理映射
        ExceptionMappingCollection exceptionMappingCollection=analysis.exceptionAnalysis(controllerAdvices);
        //将解析后的映射添加到映射扩展中
        getUrlMappingCollection().addExpand(jarExpand,urlMappingCollection);
        getExceptionMappingCollection().addExpand(jarExpand,exceptionMappingCollection);
        urlMappingCollection.initRun();
        return true;
    }

}
