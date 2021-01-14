package com.lucky.web.webfile;

import com.lucky.utils.io.file.FileUtils;
import com.lucky.web.core.Model;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;


/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/18 11:30
 */
public class WebFileUtils extends FileUtils {

    public static void download(HttpServletResponse response, File in) throws IOException {
        //设置文件下载头
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(in.getName(), "UTF-8"));
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        copy(copyToByteArray(in), out);
    }

    public static void download(HttpServletResponse response, InputStream in, String fileName) throws IOException {
        //设置文件下载头
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        copy(in, out);
    }

    public static void download(HttpServletResponse response, byte[] in, String fileName) throws IOException {
        //设置文件下载头
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        //1.设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        copy(in, out);
    }


    public static void preview(Model model, File in) throws IOException {
        HttpServletResponse resp = model.getResponse();
        if (StaticResourceManage.isStaticResource(resp, in.getName())) {
            if (in.exists()) {
                byte[] buffer = copyToByteArray(in);
                ServletOutputStream outputStream = resp.getOutputStream();
                copy(buffer, outputStream);
            }
        } else {
            model.error("403","未知格式的文件，无法预览！", "格式未知的文件: " + in.getName());
        }
    }

    public static void preview(Model model, InputStream in, String fileName) throws IOException {
        HttpServletResponse resp = model.getResponse();
        if (StaticResourceManage.isStaticResource(resp, fileName)) {
            byte[] buffer = copyToByteArray(in);
            ServletOutputStream outputStream = resp.getOutputStream();
            copy(buffer, outputStream);
        } else {
            model.error("403", "未知格式的文件，无法预览！", "格式未知的文件: " + fileName);
        }
    }

    public static void preview(Model model, byte[] in, String fileName) throws IOException {
        HttpServletResponse resp = model.getResponse();
        if (StaticResourceManage.isStaticResource(resp, fileName)) {
            ServletOutputStream outputStream = resp.getOutputStream();
            copy(in, outputStream);
        } else {
            model.error("403", "未知格式的文件，无法预览！", "格式未知的文件: " + fileName);
        }
    }
}
