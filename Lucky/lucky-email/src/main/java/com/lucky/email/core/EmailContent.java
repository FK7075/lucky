package com.lucky.email.core;

import javax.activation.DataSource;
import javax.mail.Address;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/9 11:31
 */
public class EmailContent {

    /**邮件标题*/
    private String subject;

    /**发件人*/
    private String from;

    /**收件人*/
    private List<Address> to;

    /**抄送人*/
    private List<Address> cc;

    /** 密码抄送人*/
    private List<Address> bcc;

    /**发送时间*/
    private Date sendDate;

    /**邮件类型*/
    private String type;

    /**文本内容*/
    private List<String> txtContent;

    /**HTML内容*/
    private List<String> htmlContent;

    /**文件内容*/
    private List<DataSource> fileContent;

    public EmailContent(){
        to=new ArrayList<>();
        cc=new ArrayList<>();
        bcc=new ArrayList<>();
        txtContent=new ArrayList<>();
        htmlContent=new ArrayList<>();
        fileContent=new ArrayList<>();

    }

    public void addTxtContent(String txtContent){
        this.txtContent.add(txtContent);
    }

    public void addHtmlContent(String htmlContent){
        this.htmlContent.add(htmlContent);
    }

    public void addFileContent(DataSource dataSource){
        this.fileContent.add(dataSource);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<Address> getTo() {
        return to;
    }

    public void setTo(List<Address> to) {
        this.to = to;
    }

    public List<Address> getCc() {
        return cc;
    }

    public void setCc(List<Address> cc) {
        this.cc = cc;
    }

    public List<Address> getBcc() {
        return bcc;
    }

    public void setBcc(List<Address> bcc) {
        this.bcc = bcc;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getTxtContent() {
        return txtContent;
    }

    public void setTxtContent(List<String> txtContent) {
        this.txtContent = txtContent;
    }

    public List<String> getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(List<String> htmlContent) {
        this.htmlContent = htmlContent;
    }

    public List<DataSource> getFileContent() {
        return fileContent;
    }

    public void setFileContent(List<DataSource> fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EmailContent{");
        sb.append("subject='").append(subject).append('\'');
        sb.append(", from='").append(from).append('\'');
        sb.append(", to=").append(to);
        sb.append(", cc=").append(cc);
        sb.append(", bcc=").append(bcc);
        sb.append(", sendDate=").append(sendDate);
        sb.append(", type='").append(type).append('\'');
        sb.append(", txtContent=").append(txtContent);
        sb.append(", htmlContent=").append(htmlContent);
        sb.append(", fileContent=").append(fileContent);
        sb.append('}');
        return sb.toString();
    }
}
