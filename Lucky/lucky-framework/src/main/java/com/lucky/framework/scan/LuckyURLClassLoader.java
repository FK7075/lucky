package com.lucky.framework.scan;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.exception.LuckyIOException;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/26 上午4:03
 */
public class LuckyURLClassLoader extends URLClassLoader {

    private static final Logger log= LogManager.getLogger(LuckyURLClassLoader.class);
    private URL url;

    public LuckyURLClassLoader(URL[] urls, ClassLoader parent){
        super(urls, parent);
        this.url=urls[0];
    }

    public Set<Class<?>> getComponentClass(){
        return findClass(url,"");
    }

    public Set<Class<?>> getComponentClass(String groupId){
        return findClass(url,groupId);
    }

    private Set<Class<?>> findClass(URL url,String groupId){
        InputStream input = null;
        groupId=groupId.replaceAll("\\.","/");
        Set<Class<?>> componentClasses=new HashSet<>(200);
        try{
            JarURLConnection conn = (JarURLConnection) url.openConnection();
            JarFile jarFile = conn.getJarFile();
            //解析jar包每一项
            Enumeration<JarEntry> en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry jarEntry = en.nextElement();
                String name = jarEntry.getName();
                //这里添加了路径扫描限制
                if (!name.startsWith(groupId)||!name.endsWith(".class")){
                    continue;
                }
                String className = name.replace(".class", "").replaceAll("/", ".");
                input = jarFile.getInputStream(jarEntry);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];
                int bytesNumRead = 0;
                while ((bytesNumRead = input.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesNumRead);
                }
                byte[] classBytes = baos.toByteArray();
                Class<?> aClass;
                try {
                    aClass = loadClass(className, classBytes);
                }catch (Throwable e){
                    System.err.println("[ERROR] --"+e.getClass().getSimpleName()+"-- CLASS LOAD ERROR："+className);
                    continue;
                }
                if(Assert.isNull(aClass)||Annotation.class.isAssignableFrom(aClass)){
                    continue;
                }
                if(AnnotationUtils.strengthenIsExist(aClass, Component.class)){
                    componentClasses.add(aClass);
                }
            }
            return componentClasses;
        } catch (IOException e) {
            throw new LuckyIOException(e);
        } finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    throw new LuckyIOException(e);
                }
            }
        }
    }



    //重写loadClass方法
    public Class<?> loadClass(String name,byte[] bytes) throws ClassNotFoundException {
        if(findLoadedClass(name)==null){
            return super.loadClass(name);
        }else{
            return defineClass(name,bytes,0,bytes.length);
        }

    }

}
