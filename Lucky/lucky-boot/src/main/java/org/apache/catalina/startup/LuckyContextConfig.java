package org.apache.catalina.startup;

import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;

import java.util.Map;


/**
 * @author fk
 * @version 1.0
 * @date 2020/12/15 0015 11:46
 */
public class LuckyContextConfig extends ContextConfig{

    @Override
    protected Map<String, WebXml> processJarsForWebFragments(WebXml application, WebXmlParser webXmlParser) {
        Map<String, WebXml> stringWebXmlMap = super.processJarsForWebFragments(application, webXmlParser);
        return super.processJarsForWebFragments(application, webXmlParser);
    }
}
