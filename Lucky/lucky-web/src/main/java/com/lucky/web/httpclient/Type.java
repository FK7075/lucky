package com.lucky.web.httpclient;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/4 0004 16:53
 */
public enum Type implements IContentType {

    JSON("application/json"),
    FROM_KV("application/x-www-form-urlencoded"),
    TXT("text/plain"),
    XML("application/xml");


    private String contentType;

    Type(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
