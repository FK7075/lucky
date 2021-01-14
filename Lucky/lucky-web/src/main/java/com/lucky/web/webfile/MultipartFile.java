package com.lucky.web.webfile;

import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.io.file.FileUtils;
import com.lucky.utils.io.file.InputStreamSource;
import com.lucky.web.core.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class MultipartFile implements InputStreamSource {

	/** 用户上传的文件对应的输入流*/
	private InputStream originalFileInputStream;
	/** 文件上传到服务器后的文件名*/
	private String uploadFileName;
	/** 文件类型*/
	private String fileType;
	/** 原始的文件名*/
	private String originalFileName;

	/**
	 *
	 * @param originalFileInputStream
	 * @param filename
	 */
	public MultipartFile(InputStream originalFileInputStream, String filename) {
		this.originalFileInputStream=originalFileInputStream;
		this.originalFileName=filename;
		this.fileType=filename.substring(filename.lastIndexOf("."));;
		this.uploadFileName=originalFileName.replaceAll(fileType,"")+"_"+new Date().getTime()+"_"+ BaseUtils.getRandomNumber() +getFileType();
	}

	/**
	 * 获得上传文件的类型
	 * @return
	 */
	public String getFileType() {
		return this.fileType;
	}
	
	/**
	 * 获得上传到服务器后的文件名
	 * @return
	 */
	public String getFileName() {
		return uploadFileName;
	}

	/**
	 *获得文件的原始文件名
	 * @return
	 */
	public String getOriginalFileName() {
		return originalFileName;
	}

	/**
	 * 设置上传后的文件名
	 * @param noSuffixFileName
	 */
	public void setFileName(String noSuffixFileName){
		uploadFileName=noSuffixFileName+getFileType();
	}
	
	/**
	 * 将文件复制到系统的任意位置上文件夹中
	 * @param filepath 绝对路径
	 * @throws IOException
	 */
	public void copyToFolder(String filepath) throws IOException {
		File file=new File(filepath);
		copyToFolder(file);
	}
	
	private void copyToFolder(File folder) throws IOException {
		if(folder.isFile()){
			throw new RuntimeException("文件上传错误！ Message : 错误的文件夹："+folder.getAbsolutePath());
		}
		if(!folder.exists())
			folder.mkdirs();
		FileOutputStream outfile=new FileOutputStream(folder.getAbsoluteFile()+File.separator+uploadFileName);//projectPath+"/"+docRelativePath+"/"+uploadFileName);
		FileUtils.copy(originalFileInputStream,outfile);
	}

	/**
	 * 获得上传文件的大小
	 * @return
	 * @throws IOException 
	 */
	public int getFileSize() throws IOException {
		return originalFileInputStream.available();
	}
	
	/**
	 * 获得文件对应的InputStream
	 * @return
	 */
	public InputStream getInputStream() {
		return originalFileInputStream;
	}

	/** 获得文件对应的byte数组 */
	public byte[] getByte() throws IOException {
		return FileUtils.copyToByteArray(originalFileInputStream);
	}

	/** 获取请求的Content-Type*/
	public String getFileContentType(){
		return new Model().getRequest().getContentType();
	}

}
