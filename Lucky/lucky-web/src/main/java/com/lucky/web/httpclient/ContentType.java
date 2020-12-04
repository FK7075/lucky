package com.lucky.web.httpclient;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/4 0004 16:53
 */
public enum ContentType implements IContentType {

    JSON("application/json;charset=UTF-8"),
    FROMKV("application/x-www-form-urlencoded");


    private String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getContentType() {
        return contentType;
    }
}
