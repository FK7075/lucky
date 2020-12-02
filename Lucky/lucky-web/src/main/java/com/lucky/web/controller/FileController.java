package com.lucky.web.controller;

import com.lucky.web.webfile.WebFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件操作的Controller基类
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/3 上午1:56
 */
public abstract class FileController extends LuckyController{

    /**
     * 文件下载
     * @param byteArray byte[]
     * @param setDownloadName 客户端显示的文件名
     * @throws IOException
     */
    protected void download(byte[] byteArray,String setDownloadName) throws IOException {
        WebFileUtils.download(response,byteArray,setDownloadName);
    }

    /**
     * 文件下载
     * @param in File对象(文件)
     * @throws IOException
     */
    protected void download(File in) throws IOException {
        WebFileUtils.download(response,in);
    }

    /**
     * 文件下载
     * @param in InputStream
     * @param setDownloadName 客户端得显示的文件名
     * @throws IOException
     */
    protected void download(InputStream in, String setDownloadName) throws IOException {
        WebFileUtils.download(response,in,setDownloadName);
    }

    /**
     * 文件预览
     * @param byteArray byte[]
     * @throws IOException
     */
    protected void preview(byte[] byteArray,String fileName) throws IOException {
        WebFileUtils.preview(model,byteArray,fileName);
    }

    /**
     * 文件预览
     * @param in File对象(文件)
     * @throws IOException
     */
    protected void preview(File in) throws IOException {
        WebFileUtils.preview(model,in);
    }

    /**
     * 文件预览
     * @param in InputStream
     * @throws IOException
     */
    protected void preview(InputStream in,String fileName) throws IOException {
        WebFileUtils.preview(model,in,fileName);
    }
}
