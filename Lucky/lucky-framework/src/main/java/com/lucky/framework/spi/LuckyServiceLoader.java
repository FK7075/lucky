package com.lucky.framework.spi;

import com.lucky.framework.exception.LuckyIOException;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.ClassUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Lucky的SPI（服务发现机制）
 * 规则：
 *  1.文件位置：META-INF/lucky/
 *  2.文 件 名：以接口的全限定名结尾即可
 *  3.文件内容：接口实现类全限定名(一行一个实现)
 * @author fk
 * @version 1.0
 * @date 2020/12/10 0010 9:52
 */
public class LuckyServiceLoader<S> implements Iterable<S> {

    private static final String PREFIX = "META-INF/lucky/";
    private final String SUFFIX;

    // The class or interface representing the service being loaded
    private final Class<S> service;

    // The class loader used to locate, load, and instantiate providers
    private final ClassLoader loader;

    private List<S> serviceImpls;

    private LuckyServiceLoader(Class<S> svc, ClassLoader cl) {
        service = Objects.requireNonNull(svc, "Service interface cannot be null");
        loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
        SUFFIX=svc.getName();
        serviceImpls=new ArrayList<>();
        findResources();
    }

    private void findResources(){
        try {
            Enumeration<URL> resources = loader.getResources(PREFIX);
            while (resources.hasMoreElements()){
                URL url = resources.nextElement();
                JarURLConnection conn = (JarURLConnection) url.openConnection();
                JarFile jarFile = conn.getJarFile();
                findJarFile(jarFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findJarFile(JarFile jarFile){
        Enumeration<JarEntry> en = jarFile.entries();
        InputStream input = null;
        try {
            while (en.hasMoreElements()) {
                JarEntry jarEntry = en.nextElement();
                String name = jarEntry.getName();
                if(name.startsWith(PREFIX)&&name.endsWith(SUFFIX)){
                    input=jarFile.getInputStream(jarEntry);
                    BufferedReader br=new BufferedReader(new InputStreamReader(input,"UTF-8"));
                    br.lines().map(str ->str.contains("#")?str.substring(0,str.indexOf("#")):str)
                            .filter(str->!Assert.isBlankString(str)&&!str.startsWith("#"))
                            .forEach((str)->{
                                str=str.trim();
                                try {
                                    Class<?> aClass = loader.loadClass(str);
                                    if(service.isAssignableFrom(aClass)){
                                        serviceImpls.add((S) ClassUtils.newObject(aClass));
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            });
                    br.close();
                    input.close();
                }
            }
        } catch (IOException e) {
            throw new LuckyIOException(e);
        }
    }

    public static <S> LuckyServiceLoader<S> load(Class<S> service, ClassLoader loader) {
        return new LuckyServiceLoader<>(service, loader);
    }


    public static <S> LuckyServiceLoader<S> load(Class<S> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return LuckyServiceLoader.load(service, cl);
    }

    @Override
    public Iterator<S> iterator() {
        return serviceImpls.iterator();
    }
}
