package com.lucky.web.controller;

import com.lucky.framework.uitls.file.ZipUtils;
import com.lucky.web.webfile.WebFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 压缩文件操作的Controller基类
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/3 上午1:59
 */
public class ZipFileController extends FileController{

    private static final Logger log = LogManager.getLogger(ZipFileController.class);

    protected static String baseDir=System.getProperty("java.io.tmpdir");
    static {
        baseDir=baseDir.endsWith(File.separator)?baseDir:baseDir+File.separator;
        baseDir+=("LUCKY_TEMP_FOLDER"+File.separator);
    }

    /**
     * 将多个文件打包为Zip包后提供给用户下载
     * @param srcFile 源文件集合
     * @param zipFileName 下载后的文件名，最终的压缩文件将以这个名字保存到客户端
     * @throws IOException
     */
    protected void downloadZip(List<File> srcFile, String zipFileName) throws IOException {
        compress(srcFile,zipFileName,".zip");
    }

    /**
     * 将多个文件打包为Zip包后提供给用户下载
     * @param srcFile 源文件集合
     * @throws IOException
     */
    protected void downloadZip(List<File> srcFile) throws IOException {
        downloadZip(srcFile,"luckyZ");
    }

    /**
     * 将多个文件打包为Zip包后提供给用户下载
     * @param srcFile 源文件集合
     * @throws IOException
     */
    protected void downloadZipByPath(List<String> srcFile) throws IOException {
        downloadZipByPath(srcFile,"luckyZ");
    }

    /**
     * 将多个文件打包为Zip包后提供给用户下载
     * @param srcFilePath 源文件路径的集合
     * @param zipFileName 下载后的文件名，最终的压缩文件将以这个名字保存到客户端
     * @throws IOException
     */
    protected void downloadZipByPath(List<String> srcFilePath,String zipFileName) throws IOException {
        List<File> srcFile = srcFilePath.stream().map(p -> new File(p)).collect(Collectors.toList());
        downloadZip(srcFile,zipFileName);
    }

    /**
     * 将多个文件打包为Jar包后提供给用户下载
     * @param srcFile 源文件集合
     * @param jarFileName 下载后的文件名，最终的压缩文件将以这个名字保存到客户端
     * @throws IOException
     */
    protected void downloadJar(List<File> srcFile,String jarFileName) throws IOException {
        compress(srcFile,jarFileName,".jar");
    }

    /**
     * 将多个文件打包为Jar包后提供给用户下载
     * @param srcFile 源文件集合
     * @throws IOException
     */
    protected void downloadJar(List<File> srcFile) throws IOException {
        downloadJar(srcFile,"luckyJ");
    }

    /**
     * 将多个文件打包为Jar包后提供给用户下载
     * @param srcFile 源文件集合
     * @throws IOException
     */
    protected void downloadJarByPath(List<String> srcFile) throws IOException {
        downloadJarByPath(srcFile,"luckyJ");
    }

    /**
     * 将多个文件打包为Jar包后提供给用户下载
     * @param srcFilePath 源文件集合
     * @param jarFileName 下载后的文件名，最终的压缩文件将以这个名字保存到客户端
     * @throws IOException
     */
    protected void downloadJarByPath(List<String> srcFilePath,String jarFileName) throws IOException {
        List<File> srcFile = srcFilePath.stream().map(p -> new File(p)).collect(Collectors.toList());
        downloadJar(srcFile,jarFileName);
    }

    /**
     * 文件压缩
     * @param srcFile 源文件集合
     * @param compressName 下载后的文件名
     * @param suffix 压缩文件的后缀
     * @throws IOException
     */
    private void compress(List<File> srcFile,String compressName,String suffix) throws IOException {
        srcFile=srcFile.stream()
                .filter((f)->{
                    if(f.exists())
                        return true;
                    log.error("当前请求的下载列表中不存在文件："+f);
                    return false;
                }).collect(Collectors.toList());
        if(srcFile==null||srcFile.isEmpty()){
            model.writer("Download failed！The file you need to download cannot be found！");
            log.error("Download failed！The file you need to download cannot be found！");
        }else{
            File zip=new File(baseDir+ UUID.randomUUID().toString()+suffix);
            File srcCopy=new File(baseDir+UUID.randomUUID().toString());
            WebFileUtils.copyFolders(srcFile,srcCopy);
            try{
                if(!zip.exists())
                    zip.createNewFile();
                ZipUtils.compress(srcCopy,zip);
                download(new FileInputStream(zip),compressName+suffix);
            }finally {
                zip.delete();
                WebFileUtils.deleteFile(srcCopy);
            }
        }
    }

    /**
     * 将DocBase中的多个文件打包后下载
     * @param docBaseFiles DocBase文件夹中的文件名数组
     * @param zipFileName 下载后的文件名
     * @throws IOException
     */
    protected void downloadZip(String[] docBaseFiles,String zipFileName) throws IOException {
        List<File> files = Stream.of(docBaseFiles).map(f -> model.getRealFile(f)).collect(Collectors.toList());
        downloadZip(files,zipFileName);
    }
}
