package com.lucky.web.controller;

import com.lucky.utils.base.Assert;
import com.lucky.web.utils.ImagAndString;
import com.lucky.web.utils.VerificationCode;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 图片验证码相关操作的Controller基类
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/3 上午2:09
 */
public abstract class VerificationCodeController extends LuckyController{

    /** 图片验证码生成工具*/
    private static VerificationCode vcCode=new VerificationCode();

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
        Object code = model.getSessionAttribute(sessionName);
        if(Assert.isNull(code)){
            return null;
        }
        String notnullCore= (String) code;
        model.getSession().removeAttribute(sessionName);
        return notnullCore;
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
        if(Assert.isNull(inputCode))return false;
        return inputCode.equals(getVerificationCode(SESSION_NAME));
    }

    /**
     * 验证码比对（默认SESSION的key）
     * @param inputCode 用户输入的验证码
     * @return
     */
    protected boolean codeInspection(String inputCode){
        if(Assert.isNull(inputCode))return false;
        return codeInspection(inputCode,this.getClass().getName());
    }

    /**
     * 验证码比对,忽略英文的大小写
     * @param inputCode 用户输入的验证码
     * @param SESSION_NAME  SESSION_NAME
     * @return
     */
    protected boolean codeInspectionIgnoreCase(String inputCode,String SESSION_NAME){
        if(Assert.isNull(inputCode))return false;
        return inputCode.equalsIgnoreCase(getVerificationCode(SESSION_NAME));
    }

    /**
     * 验证码比对,忽略英文的大小写（默认SESSION的key）
     * @param inputCode 用户输入的验证码
     * @return
     */
    protected boolean codeInspectionIgnoreCase(String inputCode){
        if(Assert.isNull(inputCode))return false;
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
