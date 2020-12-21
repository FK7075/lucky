package com.lucky.thymeleaf.template;


import com.lucky.utils.file.Resources;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

import java.io.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/1 12:11 下午
 */
public class ClasspathTemplateResource implements ITemplateResource {

    private final String path;
    private final String characterEncoding;

    public ClasspathTemplateResource(String path, String characterEncoding) {
        super();
        this.path = path;
        this.characterEncoding = characterEncoding;
    }

    @Override
    public String getDescription() {
        return this.path;
    }

    @Override
    public String getBaseName() {
        return computeBaseName(this.path);
    }

    @Override
    public boolean exists() {
        return Resources.getInputStream(this.path)!=null;
    }

    @Override
    public Reader reader() throws IOException {
        InputStream inputStream= Resources.getInputStream(this.path);
        if(inputStream==null){
            throw new FileNotFoundException(String.format("ClassPath resource \"%s\" does not exist", this.path));
        }
        return new BufferedReader(new InputStreamReader(inputStream,characterEncoding));
    }

    @Override
    public ITemplateResource relative(String relativeLocation) {
        Validate.notEmpty(relativeLocation, "Relative Path cannot be null or empty");

        final String fullRelativeLocation = computeRelativeLocation(this.path, relativeLocation);
        return new ClasspathTemplateResource(fullRelativeLocation,characterEncoding);
    }

    private String computeBaseName(final String path) {

        if (path == null || path.length() == 0) {
            return null;
        }

        // First remove a trailing '/' if it exists
        final String basePath = (path.charAt(path.length() - 1) == '/'? path.substring(0,path.length() - 1) : path);

        final int slashPos = basePath.lastIndexOf('/');
        if (slashPos != -1) {
            final int dotPos = basePath.lastIndexOf('.');
            if (dotPos != -1 && dotPos > slashPos + 1) {
                return basePath.substring(slashPos + 1, dotPos);
            }
            return basePath.substring(slashPos + 1);
        } else {
            final int dotPos = basePath.lastIndexOf('.');
            if (dotPos != -1) {
                return basePath.substring(0, dotPos);
            }
        }

        return (basePath.length() > 0? basePath : null);

    }

    private String computeRelativeLocation(final String location, final String relativeLocation) {
        final int separatorPos = location.lastIndexOf('/');
        if (separatorPos != -1) {
            final StringBuilder relativeBuilder = new StringBuilder(location.length() + relativeLocation.length());
            relativeBuilder.append(location, 0, separatorPos);
            if (relativeLocation.charAt(0) != '/') {
                relativeBuilder.append('/');
            }
            relativeBuilder.append(relativeLocation);
            return relativeBuilder.toString();
        }
        return relativeLocation;
    }
}
