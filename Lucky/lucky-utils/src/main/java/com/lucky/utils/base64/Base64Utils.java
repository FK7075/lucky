package com.lucky.utils.base64;

import com.lucky.utils.io.file.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Base64;

/**
 * Base64编码解码工具，基于Java8
 * @author fk7075
 * @version 1.0
 * @date 2020/9/15 9:51
 */
public abstract class Base64Utils {

    /**解码器*/
    private static final Base64.Decoder decoder = Base64.getDecoder();
    /**编码器*/
    private static final Base64.Encoder encoder = Base64.getEncoder();

    public static String FORMAT="UTF-8";

    /**
     * 对byte[]进行Base64编码
     * @param data byte数组
     * @return
     */
    public static String encoder(byte[] data){
        return encoder.encodeToString(data);
    }

    public static String encoder(InputStream in) throws IOException {
        return encoder(IOUtils.toByteArray(in));
    }

    public static String encoder(Reader in) throws IOException {
        return encoder(IOUtils.toByteArray(in));
    }

    public static String encoder(File in) throws IOException {
        return encoder(IOUtils.toByteArray(new FileInputStream(in)));
    }

    public static String encoder(URL in) throws IOException {
        return encoder(IOUtils.toByteArray(in));
    }

    public static String encoder(URI in) throws IOException {
        return encoder(IOUtils.toByteArray(in));
    }

    public static String encoder(String in) throws IOException {
        return encoder(in.getBytes(FORMAT));
    }

    public static String decoder(String base64Str) throws UnsupportedEncodingException {
        byte[] decode = decoder.decode(base64Str);
        return new String(decode,FORMAT);
    }

    public static void decoder(String base64Str,File out) throws IOException {
        byte[] decode = decoder.decode(base64Str);
        FileUtils.copy(decode,out);
    }

    public static void decoder(String base64Str,OutputStream out) throws IOException {
        byte[] decode = decoder.decode(base64Str);
        FileUtils.copy(decode,out);
    }

    public static void decoder(String base64Str,Writer out) throws IOException {
        byte[] decode = decoder.decode(base64Str);
        IOUtils.write(decode,out);
    }

}
