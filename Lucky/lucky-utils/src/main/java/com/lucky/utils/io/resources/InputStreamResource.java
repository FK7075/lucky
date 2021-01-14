package com.lucky.utils.io.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/14 0014 15:45
 */
public class InputStreamResource implements Resource {

    private ClassLoader classLoader;
    private final String path;

    public InputStreamResource(ClassLoader classLoader, String path) {
        this.classLoader = classLoader;
        this.path = path;
    }

    public InputStreamResource(String path) {
        this(new DefaultResourceLoader().getClassLoader(),path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public URL getURL() throws IOException {
        return null;
    }

    @Override
    public URI getURI() throws IOException {
        return null;
    }
}
