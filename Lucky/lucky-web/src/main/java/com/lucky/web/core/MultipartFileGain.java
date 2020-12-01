package com.lucky.web.core;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/1 0001 12:51
 */
public abstract class MultipartFileGain {

    public static Map<String, List<FileItem>> getMultipartFileMap(Model model) throws FileUploadException {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        if (!ServletFileUpload.isMultipartContent(model.getRequest())) {
            return null;
        }
        List<FileItem> list = upload.parseRequest(model.getRequest());
        //同名分组
        return list.stream().collect(Collectors.groupingBy(FileItem::getFieldName));
    }
}
