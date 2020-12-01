package com.lucky.web.core;

import com.lucky.framework.proxy.ASMUtil;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.reflect.ParameterUtils;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.exception.FileSizeCrossingException;
import com.lucky.web.exception.RequestFileSizeCrossingException;
import com.lucky.web.webfile.MultipartFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/23 10:15
 */
public abstract class MultipartFileToModel {


    /**
     * 基于MultipartFile的多文件上传
     * @param model  Model对象
     * @param webCfg Web配置类
     * @param method 将要执行的Controller方法
     * @return 由Controller方法参数名和其对应的值所组成的Map(针对MultipartFile)
     * @throws IOException
     * @throws ServletException
     */
    private static void multipartFileSetting(Model model, WebConfig webCfg, Method method)
            throws IOException, FileUploadException, FileSizeCrossingException, RequestFileSizeCrossingException {
        Parameter[] parameters = method.getParameters();
        String[] paramNames = ASMUtil.getMethodParamNames(method);
        List<String> paramlist = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            if (MultipartFile.class == parameters[i].getType() || MultipartFile[].class == parameters[i].getType()) {
                paramlist.add(ParameterUtils.getParamName(parameters[i], paramNames[i]));
            }
        }
        setMultipartFileToModel(model,webCfg);
    }



    /**
     * MultipartFile的多文件上传,基于Apache [commons-fileupload-1.3.1.jar  commons-io-2.4.jar]
     * @param model Model对象
     * @param webCfg Web配置类
     */
    public final static void setMultipartFileToModel(Model model, WebConfig webCfg) throws FileUploadException, IOException, FileSizeCrossingException, RequestFileSizeCrossingException {
        Map<String, List<FileItem>> sameNameFileItemMap = MultipartFileGain.getMultipartFileMap(model);
        if(Assert.isEmptyMap(sameNameFileItemMap)){
            return;
        }
        Set<String> fieldNames = sameNameFileItemMap.keySet();
        List<FileItem> fileItemList;
        MultipartFile[] multipartFiles;
        for (String fn : fieldNames) {
            fileItemList = sameNameFileItemMap.get(fn);
            boolean isFile = false;
            multipartFiles = new MultipartFile[fileItemList.size()];
            int fileIndex = 0;
            for (FileItem item : fileItemList) {
                if (!item.isFormField()) {
                    String filename = item.getName();
                    isFile = true;
                    InputStream in = item.getInputStream();
                    if(in.available()/1024>webCfg.getMultipartMaxFileSize()) {
                        throw new FileSizeCrossingException("单个文件超过最大上传限制："+webCfg.getMultipartMaxFileSize()+"kb");
                    }
                    MultipartFile mfp = new MultipartFile(in,filename);
                    multipartFiles[fileIndex] = mfp;
                    fileIndex++;
                } else {
                    if (!model.parameterMapContainsKey(item.getFieldName())) {
                        String[] values = {new String(item.get(), "UTF-8")};
                        model.addParameter(fn, values);
                    }
                }
            }
            if (isFile){
                double totalSize=0;
                for (MultipartFile mu : multipartFiles) {
                    totalSize+=mu.getFileSize();
                }
                if(totalSize/1024>webCfg.getMultipartMaxRequestSize()){
                    throw new RequestFileSizeCrossingException("总文件超过最大上传限制："+webCfg.getMultipartMaxRequestSize()+"kb");
                }else{
                    model.addMultipartFile(fn, multipartFiles);
                }
            }
        }
    }
}
