package com.lucky.framework.scan;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/26 上午4:03
 */
public class LuckyURLClassLoader extends URLClassLoader {
    
    public LuckyURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public LuckyURLClassLoader(URL[] urls) {
        super(urls);
    }

    public LuckyURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }
}
