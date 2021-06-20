package com.lucky.web.httpclient;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lucky.utils.serializable.json.JSONArray;
import com.lucky.utils.serializable.json.JSONObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;
import sun.security.pkcs.ParsingException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

/**
 * 响应体
 * @author fk7075
 * @version 1.0.0
 * @date 2021/6/11 下午10:47
 */
public class ResponseEntity {

    private String strResult;
    private byte[] byteResult;
    private final Header bodyHeader;
    private final Header[] allHeader;
    private final ProtocolVersion protocolVersion;
    private final Locale locale;
    private final StatusLine statusLine;
    private final int code;

    public ResponseEntity(HttpResponse response) throws IOException {
        HttpEntity responseEntity = response.getEntity();
        initBody(responseEntity);
        this.bodyHeader = responseEntity.getContentType();
        this.allHeader = response.getAllHeaders();
        this.protocolVersion = response.getProtocolVersion();
        this.locale = response.getLocale();
        this.statusLine = response.getStatusLine();
        this.code = statusLine.getStatusCode();
    }

    public Header[] getAllHeader() {
        return allHeader;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public Locale getLocale() {
        return locale;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public int getCode() {
        return code;
    }

    public byte[] getByteBody() throws IOException {
        return byteResult;
    }

    public InputStream getInputStreamBody() throws IOException {
        return new ByteArrayInputStream(getByteBody());
    }

    public String getStringBody() throws IOException {
        return strResult;
    }

    public JSONObject getJsonObjectBody(){
       return new JSONObject(strResult);
    }

    public JSONArray getJSONArrayBody(){
        return new JSONArray(strResult);
    }

    public <T> T getEntityBody(Class<T> entityClass) throws IOException {
        if(isJsonResponse()){
            return new Gson().fromJson(getStringBody(),entityClass);
        }
        if(isXmlResponse()){
            XStream xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            XStream.setupDefaultSecurity(xstream);
            return (T) xstream.fromXML(getStringBody());
        }
        if(bodyHeader == null || bodyHeader.getValue() == null){
            throw new ParsingException("解析远程结果异常！Lucky现不支json/xml以外类型结果的转化");
        }
        throw new ParsingException("解析远程结果异常！无法将 "+bodyHeader.getValue() +" 类型的结果转化为Java实体");
    }

    public <T> T getEntityBody(Type type) throws IOException {
        if(isJsonResponse()){
            return new Gson().fromJson(getStringBody(),type);
        }
        if(isXmlResponse()){
            XStream xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
            XStream.setupDefaultSecurity(xstream);
            return (T) xstream.fromXML(getStringBody());
        }
        if(bodyHeader == null || bodyHeader.getValue() == null){
            throw new ParsingException("解析远程结果异常！Lucky现不支json/xml以外类型结果的转化");
        }
        throw new ParsingException("解析远程结果异常！无法将 "+bodyHeader.getValue() +" 类型的结果转化为Java实体");
    }

    private boolean isJsonResponse(){
        if(bodyHeader == null || bodyHeader.getValue() == null){
            return false;
        }
        String JSON = "APPLICATION/JSON";
        String BODY_HEADER = bodyHeader.getValue().toUpperCase();
        return BODY_HEADER.contains(JSON);
    }

    private boolean isXmlResponse(){
        if(bodyHeader == null || bodyHeader.getValue() == null){
            return false;
        }
        String XML_1 = "APPLICATION/XML";
        String XML_2 = "TEXT/XML";
        String BODY_HEADER = bodyHeader.getValue().toUpperCase();
        return BODY_HEADER.contains(XML_1) || BODY_HEADER.contains(XML_2);
    }

    void initBody(
            final HttpEntity entity) throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        final InputStream instream = entity.getContent();
        if (instream == null) {
            strResult = null;
            byteResult = null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                    "HTTP entity too large to be buffered in memory");
            int i = (int)entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }

            //确定编码格式
            Charset charset = null;
            try {
                final ContentType contentType = ContentType.get(entity);
                if (contentType != null) {
                    charset = contentType.getCharset();
                }
            } catch (final UnsupportedCharsetException ex) {
                throw new UnsupportedEncodingException(ex.getMessage());
            }
            if (charset == null) {
                charset = StandardCharsets.UTF_8;
            }
            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            final BufferedInputStream bufferIn = new BufferedInputStream(instream);

            final ByteArrayBuffer byteBuffer = new ByteArrayBuffer(i);
            final byte[] byteTemp = new byte[4096];
            int byteL;
            while((byteL = bufferIn.read(byteTemp)) != -1) {
                byteBuffer.append(byteTemp, 0, byteL);
            }
            byteResult = byteBuffer.toByteArray();
            strResult = new String(byteResult,charset);
        } finally {
            assert instream != null;
            instream.close();
        }
    }
}
