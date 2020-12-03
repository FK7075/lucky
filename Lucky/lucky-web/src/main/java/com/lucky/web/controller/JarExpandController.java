package com.lucky.web.controller;

import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.SingletonContainer;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.scan.LuckyURLClassLoader;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.ControllerAdvice;
import com.lucky.web.annotation.RestController;
import com.lucky.web.exception.AddMappingExpandException;
import com.lucky.web.mapping.DefaultMappingAnalysis;
import com.lucky.web.mapping.ExceptionMappingCollection;
import com.lucky.web.mapping.UrlMappingCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger log = LogManager.getLogger("c.l.web.controller.JarExpandController");

    /**
     *
     * 添加一个外部的JAR包扩展
     * @param beanFactories BeanFactory集合
     * @param expandName 扩展名
     * @param jarFileUrl jar包的路径「jar:file:/绝对路径!/」「jar:http://网络路径」
     * @throws IOException
     */
    protected void andExpandJar(Set<IOCBeanFactory> beanFactories,String expandName,String jarFileUrl,String groupId)throws IOException {
        andExpandJar(beanFactories, expandName, new URL(jarFileUrl),groupId);
    }

    /**
     * 添加一个外部的JAR包扩展
     * @param beanFactories beanFantory集合
     * @param expandName 扩展名
     * @param jarUrl jar包的路径
     * @param groupId 项目的组织ID【最上层的包】
     * @throws IOException
     */
    protected void andExpandJar(Set<IOCBeanFactory> beanFactories,String expandName,URL jarUrl,String groupId) throws IOException {
        if(Assert.isNull(expandName)){
            throw new AddMappingExpandException("扩展名为NULL！");
        }
        groupId=Assert.isNull(groupId)?"":groupId;
        Set<String> deleteExpandURL = model.getUrlMappingCollection().getDeleteExpand();
        Set<String> deleteExpandEXP = model.getExceptionMappingCollection().getDeleteExpand();
        if(deleteExpandURL.contains(expandName)||deleteExpandEXP.contains(expandName)){
            deleteExpandURL.remove(expandName);
            deleteExpandEXP.remove(expandName);
            log.info("扩展集 `{}` 恢复使用！",expandName);
        }else{
            Map<String, UrlMappingCollection> urlMap = model.getUrlMappingCollection().getExpandMap();
            Map<String, ExceptionMappingCollection> expMap = model.getExceptionMappingCollection().getExpandMap();
            if(urlMap.containsKey(expandName)||expMap.containsKey(expandName)){
                log.warn("扩展集 `{}` 已存在，无法重复添加！",expandName);
            }else{
                add(beanFactories, expandName, jarUrl,groupId);
            }
        }
    }

    /**
     * 移除一个外部扩展
     * @param expandName 扩展名
     */
    protected void deleteExpandJar(String expandName){
        model.getUrlMappingCollection().deleteExpand(expandName);
        model.getExceptionMappingCollection().deleteExpand(expandName);
    }


    private final boolean add(Set<IOCBeanFactory> beanFactories,String expandName,String jarFilePath,String groupId) throws IOException {
        URL url=new URL(jarFilePath);
        return add(beanFactories, expandName, url,groupId);
    }


    private final boolean add(Set<IOCBeanFactory> beanFactories,String expandName,URL jarUrl,String groupId) throws IOException {
        URL[] urls={jarUrl};
        URLClassLoader loader = new URLClassLoader(
                urls, Thread.currentThread().getContextClassLoader());
        //创建LuckyURLClassLoader,用于获取目标jar包中的所有IOC组件
        LuckyURLClassLoader luckyURLClassLoader=new LuckyURLClassLoader(urls,loader);
        //获取当前IOC的上下文对象
        AutoScanApplicationContext applicationContext=AutoScanApplicationContext.create();
        //使用当前上下文对象动态的加载由LuckyURLClassLoader扫描得到的jar包中的IOC组件
        SingletonContainer singletonPool = applicationContext.getNewSingletonPool(beanFactories,luckyURLClassLoader.getComponentClass(groupId));
        //构造Lucky的Mapping解析器
        DefaultMappingAnalysis analysis = new DefaultMappingAnalysis();
        List<Module> controllers = singletonPool.getBeanByAnnotation(Controller.class, RestController.class);
        //解析得到所有的请求映射
        UrlMappingCollection urlMappingCollection =analysis.analysis(controllers);
        List<Module> controllerAdvices = singletonPool.getBeanByAnnotation(ControllerAdvice.class);
        //解析得到所有的异常处理映射
        ExceptionMappingCollection exceptionMappingCollection=analysis.exceptionAnalysis(controllerAdvices);
        //将解析后的映射添加到映射扩展中
        model.getUrlMappingCollection().addExpand(expandName,urlMappingCollection);
        model.getExceptionMappingCollection().addExpand(expandName,exceptionMappingCollection);
        return true;
    }

}
