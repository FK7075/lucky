package com.lucky.web.controller;

import com.google.gson.reflect.TypeToken;
import com.lucky.framework.uitls.file.Resources;

import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/8 0008 15:52
 */
public class JarExpand {

    private String expandName;
    private String groupId;
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
        return (List<JarExpand>) Resources.getObject(typeToken, classpath);
    }
}
