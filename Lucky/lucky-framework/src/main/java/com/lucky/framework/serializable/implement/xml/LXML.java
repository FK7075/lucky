package com.lucky.framework.serializable.implement.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.*;
import java.net.URL;

public class LXML {

    private static XStream xstream;

    private static final String HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    static {
        xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
        XStream.setupDefaultSecurity(xstream);
//        xstream.allowTypes(allowTypes);
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