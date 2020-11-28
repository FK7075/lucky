package com.lucky.framework.serializable.implement.xml;

import com.lucky.framework.AutoScanApplicationContext;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.*;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

public class LXML {

    private static XStream xstream;

    private static final String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    static {
        xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
        XStream.setupDefaultSecurity(xstream);

        Set<Class<?>> allowClasses = AutoScanApplicationContext.create().getBeanByType("xStream")
                .stream()
                .map(o->o.getClass())
                .collect(Collectors.toSet());
        allowClasses.stream().forEach((c)-> {
            XStreamAllowType xst=c.getAnnotation(XStreamAllowType.class);
            String alias="XStream-XFL-FK@0922@0721".equals(xst.value())
                    ?c.getSimpleName():xst.value();
            xstream.alias(alias,c);
        });
        Class<?>[] allowTypes = new Class[allowClasses.size()];
        allowClasses.toArray(allowTypes);
        xstream.allowTypes(allowTypes);
    }

    public String toXml(Object pojo) {
        return HEAD + xstream.toXML(pojo);
    }

    public void toXml(Object pojo, Writer writer) {
        xstream.toXML(pojo, writer);
    }

    public void toXml(Object pojo, OutputStream out) {
        xstream.toXML(pojo, out);
    }

    public void marshal(Object pojo, HierarchicalStreamWriter writer) {
        xstream.marshal(pojo, writer);
    }

    public void marshal(Object pojo, HierarchicalStreamWriter writer, DataHolder dataHolder) {
        xstream.marshal(pojo, writer, dataHolder);
    }

    public Object fromXml(String xml) {
        return xstream.fromXML(xml);
    }

    public Object fromXml(Reader reader) {
        return xstream.fromXML(reader);
    }

    public Object fromXml(InputStream in) {
        return xstream.fromXML(in);
    }

    public Object fromXml(URL url) {
        return xstream.fromXML(url);
    }

    public Object fromXml(File file) {
        return xstream.fromXML(file);
    }
}