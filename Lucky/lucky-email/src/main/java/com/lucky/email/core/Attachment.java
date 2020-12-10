package com.lucky.email.core;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/7 16:41
 */
public class Attachment {

    private List<File> fileList;

    private Map<String, URL> urlMap;

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public Map<String, URL> getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(Map<String, URL> urlMap) {
        this.urlMap = urlMap;
    }

    public Attachment() {
        init();
    }

    public Attachment(List<File> fileList, Map<String, URL> urlMap) {
        this.fileList = fileList;
        this.urlMap = urlMap;
    }

    private void init(){
        fileList=new ArrayList<>();
        urlMap=new HashMap<>();
    }

    public void addFile(File...files){
        fileList.addAll(Arrays.asList(files));
    }

    public void addFile(List<File> fileList){
        fileList.addAll(fileList);
    }

    public void addFile(String...filePaths){
        File[] files=new File[filePaths.length];
        for (int i = 0,j=filePaths.length; i < j; i++) {
            files[i]=new File(filePaths[i]);
        }
        addFile(files);
    }

    public void addURL(URL url,String name){
        urlMap.put(name,url);
    }

}
