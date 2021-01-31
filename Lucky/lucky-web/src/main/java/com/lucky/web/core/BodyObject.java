package com.lucky.web.core;

import com.lucky.utils.file.FileUtils;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/5 下午6:09
 */
public class BodyObject {

    private String bodyObject;
    private String contentType;

    public BodyObject(String bodyObject,String contentType) {
        this.bodyObject = bodyObject;
        this.contentType=contentType;
    }

    public BodyObject(HttpServletRequest request){
        final ServletInputStream inputStream;
        try {
            inputStream = request.getInputStream();
            String contentType = new ServletRequestContext(request).getContentType();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            StringWriter sw = new StringWriter();
            int ch;
            while ((ch = br.read()) != -1) {
                sw.write(ch);
            }
            this.bodyObject = sw.toString();
            this.contentType=contentType;
        } catch (IOException e) {
           throw new RuntimeException(e);
        }

    }

    public String getBodyObject() {
        return bodyObject;
    }

    public void setBodyObject(String bodyObject) {
        this.bodyObject = bodyObject;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s",contentType,bodyObject);
    }
}
