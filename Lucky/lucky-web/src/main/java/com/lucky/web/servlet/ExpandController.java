package com.lucky.web.servlet;

import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.container.Module;
import com.lucky.framework.container.SingletonContainer;
import com.lucky.framework.container.enums.Strategy;
import com.lucky.framework.container.factory.IOCBeanFactory;
import com.lucky.framework.scan.LuckyURLClassLoader;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.ControllerAdvice;
import com.lucky.web.annotation.RestController;
import com.lucky.web.beanfactory.LuckyWebBeanFactory;
import com.lucky.web.core.Model;
import com.lucky.web.mapping.DefaultMappingAnalysis;
import com.lucky.web.mapping.ExceptionMappingCollection;
import com.lucky.web.mapping.UrlMappingCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/2 0002 15:33
 */
public abstract class ExpandController {

    private static final Logger log = LogManager.getLogger(ExpandController.class);

    /** 当前请求的Model对象*/
    protected Model model;

    protected void andExpandJar(Set<IOCBeanFactory> beanFactories,String expandName,String expandJarFilePath) throws IOException {
        if(Assert.isNull(beanFactories)){
//            throw new
        }
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
                add(beanFactories, expandName, expandJarFilePath);
            }
        }
    }

    protected void deleteExpandJar(String expandName){
        model.getUrlMappingCollection().deleteExpand(expandName);
        model.getExceptionMappingCollection().deleteExpand(expandName);
    }


    private boolean add(Set<IOCBeanFactory> beanFactories,String expandName,String expandJarFilePath) throws IOException {
        URL[] urls={new URL(expandJarFilePath)};
        URLClassLoader loader = new URLClassLoader(
                urls, Thread.currentThread().getContextClassLoader());
        //创建LuckyURLClassLoader,用于获取目标jar包中的所有IOC组件
        LuckyURLClassLoader luckyURLClassLoader=new LuckyURLClassLoader(urls,loader);
        //获取当前IOC的上下文对象
        AutoScanApplicationContext applicationContext=AutoScanApplicationContext.create();
        //使用当前上下文对象动态的加载由LuckyURLClassLoader扫描得到的jar包中的IOC组件
        SingletonContainer singletonPool = applicationContext.getNewSingletonPool(beanFactories,luckyURLClassLoader.getComponentClass());
        //构造Lucky的Mapping解析器
        DefaultMappingAnalysis analysis = new DefaultMappingAnalysis();
        List<Module> controllers = singletonPool.getBeanByAnnotation(Controller.class, RestController.class);
        //解析得到所有的请求映射
        UrlMappingCollection urlMappingCollection =analysis.analysis(controllers);
        List<Module> controllerAdvices = singletonPool.getBeanByAnnotation(ControllerAdvice.class);
        //解析得到所有的异常处理映射
        ExceptionMappingCollection exceptionMappingCollection=analysis.exceptionAnalysis(controllerAdvices);
        //将解析后的映射合并到当前上下文中
        model.getUrlMappingCollection().addExpand(expandName,urlMappingCollection);
        model.getExceptionMappingCollection().addExpand(expandName,exceptionMappingCollection);
        return true;
    }

}
