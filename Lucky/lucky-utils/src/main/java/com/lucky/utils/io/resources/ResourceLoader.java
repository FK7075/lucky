package com.lucky.utils.io.resources;

import com.lucky.utils.annotation.Nullable;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/14 0014 15:52
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    @Nullable
    ClassLoader getClassLoader();

    Resource getResource(String location);

}
