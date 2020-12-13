package com.lucky.framework.scan;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/13 下午10:34
 */
public class LuckyScan {

    private ClassLoader loader;

    public static void main(String[] args) throws IOException {
        Enumeration<URL> resources =
                Thread.currentThread().getContextClassLoader().getResources("META-INF/services/");
        while (resources.hasMoreElements()){
            System.out.println(resources.nextElement());
        }
    }
}
