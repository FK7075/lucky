package com.lucky.framework.scan;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.exception.LuckyIOException;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 组件Bean的Class -> 扫描Jar包中的所有 .class文件，并过滤掉没有没@Conponent注解标注的Class
 * BeanFactory    -> 扩展外部Jar包需要的BeanFactory为特殊的BeanFactory，这些BeanFactory
 * 可操作的组件Class和插件Class的范围是不同的！
 * 这些特殊的BeanFactory配置在各个框架组件的 META-INF/jarexpands/**_factory.lucky中
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/26 上午4:03
 */
public class LuckyURLClassLoader extends URLClassLoader {

    private static final Logger log= LoggerFactory.getLogger(LuckyURLClassLoader.class);
    private static final String JAR_EXPAND_FILE_PREFIX="META-INF/jarexpands/";
    private static final String JAR_EXPAND_FILE_SUFFIX="factory.lucky";
    private URL url;

    public LuckyURLClassLoader(URL[] urls, ClassLoader parent){
        super(urls, parent);
        this.url=urls[0];
    }

    public JarExpandChecklist getComponentClass(){
        return findClass(url,"");
    }

    public JarExpandChecklist getComponentClass(String groupId){
        return findClass(url,groupId);
    }

    private JarExpandChecklist findClass(URL url,String groupId){
        groupId=groupId.replaceAll("\\.","/");
        JarExpandChecklist jar=new JarExpandChecklist();
        try{
            JarURLConnection conn = (JarURLConnection) url.openConnection();
            JarFile jarFile = conn.getJarFile();
            //解析jar包每一项
            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry jarEntry = en.nextElement();
                String name = jarEntry.getName();

                if(name.startsWith(JAR_EXPAND_FILE_PREFIX)&&name.endsWith(JAR_EXPAND_FILE_SUFFIX)){
                    InputStream input = jarFile.getInputStream(jarEntry);
                    BufferedReader br=new BufferedReader(new InputStreamReader(input,"UTF-8"));
                    br.lines().map(str ->str.contains("#")?str.substring(0,str.indexOf("#")):str)
                              .filter(str->!Assert.isBlankString(str)&&!str.startsWith("#"))
                              .forEach((str)->{
                        String[] split = str.split(":");
                        jar.addBeanFactory(this,split[0].trim(),split[1].trim());
                    });
                    br.close();
                    input.close();
                    continue;
                }

                //这里添加了路径扫描限制
                if (!name.startsWith(groupId)||!name.endsWith(".class")){
                    continue;
                }
                String className = name.replace(".class", "").replaceAll("/", ".");
                Class<?> aClass;
                try {
                    aClass = loadClass(className);
                }catch (Throwable e){
                    System.err.println("[ERROR] --"+e.getClass().getSimpleName()+"-- CLASS LOAD ERROR："+className);
                    continue;
                }
                if(Assert.isNull(aClass)||Annotation.class.isAssignableFrom(aClass)){
                    continue;
                }
                if(AnnotationUtils.strengthenIsExist(aClass, Component.class)){
                    jar.addBeanClass(aClass);
                }
            }
            return jar;
        } catch (IOException e) {
            throw new LuckyIOException(e);
        }
    }

}
