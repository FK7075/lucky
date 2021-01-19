package com.lucky.web.core;

import com.lucky.utils.file.*;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/19 0019 16:17
 */
public class RequestBodyParam {

    private String contentType;
    private String requestBody;

    public RequestBodyParam(HttpServletRequest request) throws IOException {
        if(ServletFileUpload.isMultipartContent(request)){
            return;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));
        StringWriter sw = new StringWriter();
        FileUtils.copy(br,sw);
        this.requestBody = sw.toString();
        this.contentType = new ServletRequestContext(request).getContentType().toUpperCase();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        if(contentType==null&&requestBody==null){
            return "{}";
        }
        return String.format("[%s] --> %s",contentType,requestBody);
    }
}
