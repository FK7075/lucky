package com.lucky.utils.io.resources;

import com.lucky.utils.io.file.InputStreamSource;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/14 0014 15:34
 */
public interface Resource extends InputStreamSource {

    boolean exists();

    URL getURL() throws IOException;

    URI getURI() throws IOException;
}
