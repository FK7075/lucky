package com.lucky.web.controller;

import com.google.gson.reflect.TypeToken;
import com.lucky.utils.file.Resources;

import java.util.List;

/**
 * j=外部jar扩展的详细信息
 * @author fk
 * @version 1.0
 * @date 2020/12/8 0008 15:52
 */
public class JarExpand {

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
        TypeToken typeToken=new TypeToken<List<JarExpand>>(){};
        return (List<JarExpand>) Resources.fromJson(typeToken, classpath);
    }

    public static List<JarExpand> getJarExpandByYaml(String classpath){
        return Resources.fromYaml(JarExpand.class,classpath);
    }
}
