package com.lucky.framework.uitls.file;

import com.google.gson.reflect.TypeToken;
import com.lucky.framework.exception.ClasspathFileLoadException;
import com.lucky.framework.serializable.implement.json.LSON;
import com.lucky.framework.serializable.implement.xml.LXML;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Type;

/**
 * 从ClassPath下获取资源
 * @author fk7075
 * @version 1.0.0
 * @date 2020/10/3 12:34 上午
 */
public abstract class Resources {

    private static LSON lson=new LSON();

    /**
     * 获取classpath下的一个InputStream
     * @param filePath 文件路径
     * @return InputStream
     */
    public static InputStream getInputStream(String filePath){
        return Resources.class.getResourceAsStream(filePath);
    }

    /**
     * 获取classpath下的一个BufferedInputStream
     * @param filePath 文件路径
     * @param charsetName 编码方式
     * @return
     */
    public static BufferedInputStream getInputStream(String filePath,String charsetName){
        return new BufferedInputStream(Resources.class.getResourceAsStream(filePath));
    }

    /**
     * 获取classpath下的一个Reader
     * @param filePath 文件路径
     * @param charsetName 编码方式
     * @return Reader
     */
    public static BufferedReader getReader(String filePath,String charsetName){
        return new BufferedReader(new InputStreamReader(getInputStream(filePath,charsetName)));
    }

    /**
     * 获取classpath下的一个Reader
     * @param filePath
     * @return Reader
     */
    public static BufferedReader getReader(String filePath){
        try {
            return new BufferedReader(new InputStreamReader(getInputStream(filePath)));
        }catch (NullPointerException e){
            throw new ClasspathFileLoadException(filePath,e);
        }

    }

    /**
     * 获取classpath下的一个Json文件，并将其转化为Java对象
     * @param tClass Java类型的Class
     * @param filePath 文件路径
     * @param <T>
     * @return
     */
    public static <T> T fromJson(Class<T> tClass, String filePath){
        return lson.fromJson(tClass,getReader(filePath,"UTF-8"));
    }

    /***
     * 获取classpath下的一个Json文件，并将其转化为Java对象
     * @param typeToken Java类型的TypeToken
     * @param filePath
     * @param <T>
     * @return
     */
    public static <T> T fromJson(TypeToken<T> typeToken, String filePath){
        return fromJson(typeToken.getType(),filePath);
    }

    /***
     * 获取classpath下的一个Json文件，并将其转化为Java对象
     * @param type Java类型Type
     * @param filePath
     * @param <T>
     * @return
     */
    public static <T> T fromJson(Type type, String filePath){
        return (T) lson.fromJson(type,getReader(filePath,"UTF-8"));
    }

    /***
     * 获取classpath下的一个XML文件，并将其转化为Java对象
     * @param filePath
     * @param <T>
     * @return
     */
    public static <T> T fromXml(String filePath){
        return (T) new LXML().fromXml(getReader(filePath));
    }

    /***
     * 获取classpath下的一个Yaml文件，并将其转化为Java对象
     * @param aClass
     * @param filePath
     * @param <T>
     * @return
     */
    public static <T> T fromYaml(Class<?> aClass,String filePath){
        return (T) new Yaml().loadAs(getReader(filePath),aClass);
    }

    /**
     * 获取classpath下的一个文件的内容
     * @param filePath 文件内容
     * @return
     */
    public static String getString(String filePath) throws IOException {
        return FileUtils.copyToString(getReader(filePath,"UTF-8"));
    }

}
