package com.lucky.web.servlet;

import com.google.gson.reflect.TypeToken;
import com.lucky.framework.dm5.MD5Utils;
import com.lucky.framework.serializable.implement.json.LSON;
import com.lucky.framework.serializable.implement.xml.LXML;
import com.lucky.framework.uitls.file.ZipUtils;
import com.lucky.web.core.Model;
import com.lucky.web.filter.ImagAndString;
import com.lucky.web.filter.VerificationCode;
import com.lucky.web.webfile.WebFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class LuckyController {

	protected static final Logger log = LogManager.getLogger(LuckyController.class);
	protected static LSON lson=new LSON();
	protected static String baseDir=System.getProperty("java.io.tmpdir");
	static {
		baseDir=baseDir.endsWith(File.separator)?baseDir:baseDir+File.separator;
		baseDir+=("LUCKY_TEMP_FOLDER"+File.separator);
	}

	/** 当前请求的Model对象*/
	protected Model model;
	/** Request对象*/
	protected HttpServletRequest request;
	/** Response对象*/
	protected HttpServletResponse response;
	/** Session对象*/
	protected HttpSession session;
	/** ServletContext对象*/
	protected ServletContext application;
	/** 图片验证码生成工具*/
	private static VerificationCode vcCode=new VerificationCode();

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
	protected void download(InputStream in,String setDownloadName) throws IOException {
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

	/**
	 * 将对象转化为JSON字符串
	 * @param pojo
	 * @return
	 */
	protected String toJson(Object pojo){
		return lson.toJson(pojo);
	}

	/**
	 * 将JSON字符串转化为Java对象
	 * @param targetClass
	 * @param jsonString
	 * @param <T>
	 * @return
	 */
	protected <T> T fromJson(Class<T> targetClass,String jsonString){
		return lson.fromJson(targetClass,jsonString);
	}

	protected <T> T fromJson(Class<T> pojoClass,Reader reader){
		return lson.fromJson(pojoClass, reader);
	}

	/**
	 * 将JSON字符串转化为Java对象
	 * @param token
	 * @param jsonString
	 * @return
	 */
	protected Object fromJson(TypeToken token,String jsonString){
		return lson.fromJson(token,jsonString);
	}

	protected Object fromJson(TypeToken token, Reader reader){
		return lson.fromJson(token.getType(),reader);
	}

	/**
	 * 将JSON字符串转化为Java对象
	 * @param type
	 * @param jsonString
	 * @return
	 */
	protected Object fromJson(Type type, String jsonString){
		return lson.fromJson(type,jsonString);
	}

	protected Object fromJson(Type type, Reader reader){
		return lson.fromJson(type,reader);
	}

	protected String toXml(Object pojo){
		return new LXML().toXml(pojo);
	}

	protected void toXml(Object pojo,Writer writer){
		new LXML().toXml(pojo,writer);
	}

	protected void toXml(Object pojo,OutputStream out){
		new LXML().toXml(pojo,out);
	}

	protected Object fromXml(String xmlStr){
		return new LXML().fromXml(xmlStr);
	}

	protected Object fromXml(Reader xmlReader){
		return new LXML().fromXml(xmlReader);
	}

	protected Object fromXml(InputStream xmlIn){
		return new LXML().fromXml(xmlIn);
	}

	/**
	 * MD5加密
	 * @param clear 明文
	 * @return 密文
	 */
	protected String md5(String clear){
		return MD5Utils.md5(clear);
	}

	/**
	 * MD5加密
	 * @param clear 明文
	 * @param salt 盐
	 * @param cycle 循环加密的次数
	 * @return
	 */
	protected String md5(String clear,String salt,int cycle){
		return MD5Utils.md5(clear,salt,cycle);
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

	/**
	 * 生成图片验证码
	 * @param sessionName SESSION_NAME
	 * @throws IOException
	 */
	protected void generateVerificationCode(String sessionName) throws IOException {
		saveVerificationCode(sessionName);
	}

	/**
	 * 生成图片验证码,使用默认的key(当前类的全路径)存储到SESSION域
	 * @throws IOException
	 */
	protected void generateVerificationCode() throws IOException {
		generateVerificationCode(this.getClass().getName());
	}


	/**
	 * 生成指定长度的图片验证码
	 * @param SESSION_NAME
	 * @param CHAR_LENGTH 验证码长度
	 * @throws IOException
	 */
	protected void generateVerificationCode(String SESSION_NAME,int CHAR_LENGTH) throws IOException {
		vcCode.CHAR_LENGTH=CHAR_LENGTH;
		vcCode.IMG_WIDTH=20*CHAR_LENGTH;
		saveVerificationCode(SESSION_NAME);
	}

	/**
	 * 获取验证码信息
	 * @return
	 */
	protected String getVerificationCode(String sessionName){
		return (String) model.getSessionAttribute(sessionName);
	}

	/**
	 * 获取验证码信息（默认SESSION的key）
	 * @return
	 */
	protected String getVerificationCode(){
		return getVerificationCode(this.getClass().getName());
	}

	/**
	 * 验证码比对，英文需要区分大小写
	 * @param inputCode 用户输入的验证码
	 * @param SESSION_NAME  SESSION_NAME
	 * @return
	 */
	protected boolean codeInspection(String inputCode,String SESSION_NAME){
		return inputCode.equals(getVerificationCode(SESSION_NAME));
	}

	/**
	 * 验证码比对（默认SESSION的key）
	 * @param inputCode 用户输入的验证码
	 * @return
	 */
	protected boolean codeInspection(String inputCode){
		return codeInspection(inputCode,this.getClass().getName());
	}

	/**
	 * 验证码比对,忽略英文的大小写
	 * @param inputCode 用户输入的验证码
	 * @param SESSION_NAME  SESSION_NAME
	 * @return
	 */
	protected boolean codeInspectionIgnoreCase(String inputCode,String SESSION_NAME){
		return inputCode.equalsIgnoreCase(getVerificationCode(SESSION_NAME));
	}

	/**
	 * 验证码比对,忽略英文的大小写（默认SESSION的key）
	 * @param inputCode 用户输入的验证码
	 * @return
	 */
	protected boolean codeInspectionIgnoreCase(String inputCode){
		return codeInspectionIgnoreCase(inputCode,this.getClass().getName());
	}

	/**
	 * 将验证码发送给客户端，并将验证码中的文字信息保存到Session域中
	 * @param SESSION_NAME
	 * @throws IOException
	 */
	private void saveVerificationCode(String SESSION_NAME) throws IOException {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		HttpSession session = request.getSession(true);
		ImagAndString imagAndString = vcCode.createVerificationCodeImage();
		session.removeAttribute(SESSION_NAME);
		session.setAttribute(SESSION_NAME, imagAndString.getStr());
		ImageIO.write(imagAndString.getImg(), "JPEG", response.getOutputStream());
	}
}
