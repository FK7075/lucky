package com.lucky.utils.fileload;

import com.lucky.utils.annotation.Nullable;
import com.lucky.utils.io.utils.ResourceUtils;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/14 0014 15:52
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    @Nullable
    ClassLoader getClassLoader();

    Resource getResource(String location);

}
