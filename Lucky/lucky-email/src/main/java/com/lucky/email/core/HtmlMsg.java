package com.lucky.email.core;

/**
 *
 * @author fk7075
 * @version 1.0
 * @date 2020/9/7 17:29
 */
@FunctionalInterface
public interface HtmlMsg {

    void setHtmlMsg(HtmlTemp htmlTemp) throws Exception;

}
