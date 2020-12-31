package com.lucky.framework.scan;

import com.google.gson.reflect.TypeToken;
import com.lucky.utils.file.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * j=外部jar扩展的详细信息
 * @author fk
 * @version 1.0
 * @date 2020/12/8 0008 15:52
 */
public class JarExpand {
    private static final Logger log= LoggerFactory.getLogger("com.lucky.framework.scan.JarExpand");
    /** 扩展名，每一个Jar扩展都应该拥有的一个唯一的扩展名*/
    private String expandName;
    /** 扩展的组织ID*/
    private String groupId;
    /** Jar扩展的位置*/
    private String jarPath;

    public JarExpand() {
    }

    public JarExpand(String expandName, String groupId, String jarPath) {
        this.expandName = expandName;
        this.groupId = groupId;
        this.jarPath = jarPath;
    }

    public String getExpandName() {
        return expandName;
    }

    public void setExpandName(String expandName) {
        this.expandName = expandName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public static List<JarExpand> getJarExpandByJson(String classpath){
        try {
            TypeToken typeToken=new TypeToken<List<JarExpand>>(){};
            return (List<JarExpand>) Resources.fromJson(typeToken, classpath);
        }catch (Exception e){
            throw new RuntimeException
            ("Json解析异常，无法从 `classpath: "+classpath+"` 文件中提取JarExpand信息！请检查文件的格式和内容是否符合如下规范！" +
                    "\n 1.文件格式：.json \n 2.文件内容：[{\"expandName\":\"xxx\",\"groupId\":\"xxx\",\"jarPath\":\"xxx\"},...,{...}]");
        }

    }

    public static List<JarExpand> getJarExpandByYaml(String classpath){
        return Resources.fromYaml(JarExpand.class,classpath);
    }

    public void printJarInfo(){
        log.info("正在添加Jar扩展,扩展信息如下：\n  expandName : {}\n  groupId    : {}\n  jarPath    : {}"
                ,expandName
                ,groupId
                ,jarPath);
    }
}
