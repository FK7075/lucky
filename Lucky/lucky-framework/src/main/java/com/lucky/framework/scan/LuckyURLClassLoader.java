package com.lucky.framework.scan;

import com.lucky.framework.annotation.Component;
import com.lucky.framework.uitls.reflect.AnnotationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/26 上午4:03
 */
public class LuckyURLClassLoader extends URLClassLoader {


    //属于本类加载器加载的jar包
    private JarFile jarFile;

    //保存已经加载过的Class对象
    private Map<String,Class> cacheClassMap = new HashMap<>();

    //保存本类加载器加载的class字节码
    private Map<String,byte[]> classBytesMap = new HashMap<>();

    //需要注册的spring bean的name集合
    private List<String> registeredBean = new ArrayList<>();


    //构造
    public LuckyURLClassLoader(URL[] urls, ClassLoader parent){
        super(urls, parent);
        URL url = urls[0];
        try {
            JarURLConnection conn = (JarURLConnection) url.openConnection();
            jarFile = conn.getJarFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //初始化类加载器执行类加载
        init();
    }



    //重写loadClass方法
    //改写loadClass方式
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if(findLoadedClass(name)==null){
            return super.loadClass(name);
        }else{
            return cacheClassMap.get(name);
        }

    }



    /**
     * 方法描述 初始化类加载器，保存字节码
     * @method init
     */
    private void init() {
        //解析jar包每一项
        Enumeration<JarEntry> en = jarFile.entries();
        InputStream input = null;
        try{
            while (en.hasMoreElements()) {
                JarEntry je = en.nextElement();
                String name = je.getName();
                //这里添加了路径扫描限制
                if (!name.endsWith(".class")){
                    continue;
                }
                String className = name.replace(".class", "").replaceAll("/", ".");
                input = jarFile.getInputStream(je);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];
                int bytesNumRead = 0;
                while ((bytesNumRead = input.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesNumRead);
                }
                byte[] classBytes = baos.toByteArray();
                classBytesMap.put(className,classBytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        //将jar中的每一个class字节码进行Class载入
        for (Map.Entry<String, byte[]> entry : classBytesMap.entrySet()) {
            String key = entry.getKey();
            Class<?> aClass = null;
            try {
                aClass = loadClass(key);
            } catch (Throwable e) {
                System.err.println(key);
                continue;
            }
            cacheClassMap.put(key,aClass);
        }

    }

    /**
     * 方法描述 初始化spring bean
     * @method initBean
     */
    public Set<Class<?>> getComponentClass(){
        Set<Class<?>> componentClassSet = new HashSet<>();
        for (Map.Entry<String, Class> entry : cacheClassMap.entrySet()) {
            Class<?> fileClass = entry.getValue();
            if(Annotation.class.isAssignableFrom(fileClass)){
                continue;
            }
            if(AnnotationUtils.strengthenIsExist(fileClass, Component.class)){
                componentClassSet.add(fileClass);
            }
        }
        return componentClassSet;
    }
}
