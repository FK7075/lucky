package com.lucky.web.core;

import com.lucky.utils.base.Assert;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.file.FileUtils;
import com.lucky.web.annotation.Upload;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.exception.FileSizeCrossingException;
import com.lucky.web.exception.FileTypeIllegalException;
import com.lucky.web.exception.RequestFileSizeCrossingException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.ServletException;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/23 10:14
 */
public class UploadAnnotationFileToModel {


    /**
     * 批量文件上传@Upload注解方式
     *
     * @param model  Model对象
     * @param method 将要执行的Controller方法
     * @return 上传后的文件名与表单name属性所组成的Map
     * @throws IOException
     * @throws ServletException
     */
    public final static void uploadAnnotationFileSetting(Model model, WebConfig wenCfg, Method method) throws IOException, FileTypeIllegalException, FileSizeCrossingException, FileUploadException, RequestFileSizeCrossingException {
        if (method.isAnnotationPresent(Upload.class)) {
            Upload upload = method.getAnnotation(Upload.class);
            String[] files = upload.names();
            String[] savePaths = upload.filePath();
            String types = upload.type();
            long maxSize = upload.maxSize();
            long totalSize=upload.totalSize();
            Map<String, String> fieldAndFolder = new HashMap<>();
            if (savePaths.length == 1) {
                for (String file : files) {
                    fieldAndFolder.put(file, savePaths[0]);
                }
            } else {
                for (int i = 0; i < savePaths.length; i++) {
                    fieldAndFolder.put(files[i], savePaths[i]);
                }
            }
            setUploadFileToModel(model, wenCfg, fieldAndFolder, types, maxSize,totalSize);
        }
    }

    /**
     * --@Upload注解方式的多文件上传-基于Apache [commons-fileupload-1.3.1.jar  commons-io-2.4.jar]
     * 适配内嵌tomcat的文件上传与下载操作
     *
     * @param model          Model对象
     * @param type           允许上传的文件类型
     * @param fileSize        允许上传文件的最大大小
     */
    private static void setUploadFileToModel(Model model, WebConfig webCfg , Map<String, String> fieldAndFolder, String type, long fileSize,long totalSize)
            throws FileTypeIllegalException, IOException, FileSizeCrossingException, FileUploadException, RequestFileSizeCrossingException {
        Map<String, List<FileItem>> sameNameFileItemMap=MultipartFileGain.getMultipartFileMap(model);
        if(Assert.isEmptyMap(sameNameFileItemMap)){
            return;
        }
        String savePath = model.getRealPath("/");
        Set<String> fieldNames = sameNameFileItemMap.keySet();
        List<FileItem> fileItemList;

        File[] uploadNames;
        for (String fn : fieldNames) {
            fileItemList = sameNameFileItemMap.get(fn);
            boolean isFile = false;
            uploadNames = new File[fileItemList.size()];
            List<UploadCopy> uploadCopyList=new ArrayList<>();
            int fileIndex = 0;
            for (FileItem item : fileItemList) {
                if (!item.isFormField()) {
                    if (fieldAndFolder.containsKey(fn)) {
                        isFile = true;
                        String filename = item.getName();
                        String suffix = filename.substring(filename.lastIndexOf("."));
                        String NoSuffix = filename.substring(0, filename.lastIndexOf("."));
                        if (!"".equals(type) && !type.toLowerCase().contains(suffix.toLowerCase())) {
                            throw new FileTypeIllegalException("上传的文件格式" + suffix + "不合法！合法的文件格式为：" + type);
                        }
                        String pathSave = fieldAndFolder.get(fn);
                        String filePath;
                        if (pathSave.startsWith("abs:")) {//绝对路径写法
                            filePath=pathSave.substring(4);
                        } else {//相对路径写法
                            filePath=savePath + pathSave;
                        }
                        filename = NoSuffix + "_" + new Date().getTime() + "_" + BaseUtils.getRandomNumber() + suffix;
                        if (filename == null || "".equals(filename.trim())) {
                            continue;
                        }
                        filePath=filePath.endsWith(File.separator)?filePath.substring(0,filePath.length()-1):filePath;
                        filePath=filePath+File.separator + filename;
                        InputStream in = item.getInputStream();
                        fileSize=fileSize==0?webCfg.getMultipartMaxFileSize():fileSize;
                        int size = in.available();
                        int filesize = size / 1024;
                        if (filesize > fileSize) {
                            throw new FileSizeCrossingException("单个上传文件的大小超出最大上传限制：" + fileSize + "kb");
                        }
                        uploadCopyList.add(new UploadCopy(in,new File(filePath)));
                        item.delete();
                        uploadNames[fileIndex] = new File(filePath);
                        fileIndex++;
                    }
                } else {
                    if (!model.parameterMapContainsKey(item.getFieldName())) {
                        String[] values = {new String(item.get(), "UTF-8")};
                        model.addParameter(item.getFieldName(), values);
                    }
                }
            }
            if (isFile){
                double msxSize=UploadCopy.getTotalSize(uploadCopyList);
                totalSize=totalSize==0?webCfg.getMultipartMaxRequestSize():totalSize;
                if(msxSize/1024>totalSize){
                    throw new RequestFileSizeCrossingException("总文件超过最大上传限制："+webCfg.getMultipartMaxRequestSize()+"kb");
                }else{
                    for (UploadCopy uploadCopy : uploadCopyList) {
                        uploadCopy.copy();
                    }
                    model.addUploadFile(fn, uploadNames);
                }
            }
        }
    }
}


class UploadCopy{
    private InputStream in;
    private File out;

    public UploadCopy(InputStream in, File out) {
        this.in = in;
        this.out = out;
    }

    public void copy() throws IOException {
        if(!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        FileUtils.copy(in,new BufferedOutputStream(new FileOutputStream(out)));
    }

    public int getFileSize() throws IOException {
        return in.available();
    }

    public static double getTotalSize(List<UploadCopy> list) throws IOException {
        long t=0;
        for (UploadCopy uploadCopy : list) {
            t+=uploadCopy.getFileSize();
        }
        return t;
    }
}
