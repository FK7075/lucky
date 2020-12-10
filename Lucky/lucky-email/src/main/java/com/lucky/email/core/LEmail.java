package com.lucky.email.core;

import com.lucky.email.conf.EmailConfig;
import org.apache.commons.mail.*;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/9/5 4:46 上午
 */
public class LEmail {


    /** 接收人邮箱 */
    private List<String> to =new ArrayList<>();

    /** 抄送人邮箱*/
    private List<String> cc =new ArrayList<>();

    /** 秘密抄送人邮箱 */
    private List<String> bcc =new ArrayList<>();

    private static EmailConfig emailConfig=EmailConfig.defaultEmailConfig();

    public LEmail(){}

    public LEmail(String toEmail) {
        to.add(toEmail);
    }

    public LEmail(String...toEmails) {
        to.addAll(Arrays.asList(toEmails));
    }

    public LEmail(List<String> toEmails) {
        to=toEmails;
    }

    public LEmail(List<String> to, List<String> cc, List<String> bcc) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
    }

    public LEmail addTo(String...toEmails){
        to.addAll(Arrays.asList(toEmails));
        return this;
    }

    public LEmail addCc(String...ccEmails){
        cc.addAll(Arrays.asList(ccEmails));
        return this;
    }

    public LEmail addBcc(String...bccEmails){
        bcc.addAll(Arrays.asList(bccEmails));
        return this;
    }

    private void init(Email email, String subject) throws EmailException {
        email.setHostName(emailConfig.getSmtpHost());
        email.setSmtpPort(emailConfig.getSmtpPort());
        email.setAuthenticator(new DefaultAuthenticator(emailConfig.getEmail(), emailConfig.getPassword()));
        email.setSSLOnConnect(true);
        email.setCharset("UTF-8");
        if(emailConfig.getUsername()==null){
            email.setFrom(emailConfig.getEmail());
        }else {
            email.setFrom(emailConfig.getEmail(),emailConfig.getUsername());
        }
        String[] toArray=new String[to.size()];
        to.toArray(toArray);
        email.addTo(toArray);
        if(!cc.isEmpty()){
            String[] ccArray=new String[cc.size()];
            cc.toArray(ccArray);
            email.addCc(ccArray);
        }
        if(!bcc.isEmpty()){
            String[] bccArray=new String[bcc.size()];
            bcc.toArray(bccArray);
            email.addCc(bccArray);
        }
        email.setSubject(subject);
    }

    /**
     * 发送一封简单的邮件
     * @param subject 主题
     * @param message 内容
     * @throws EmailException
     */
    public void sendSimpleEmail(String subject, String message) throws EmailException {
        SimpleEmail email = new SimpleEmail();
        init(email,subject);
        email.setMsg(message);
        email.send();
    }

    /**
     * 发送一封带附件的邮件
     * @param subject 主题
     * @param message 内容
     * @param attFile 附件(附件为本地资源)
     * @throws EmailException
     */
    public void sendAttachmentEmail(String subject, String message, File...attFile) throws EmailException {
        // Create the email message
        MultiPartEmail email = new MultiPartEmail();
        init(email,subject);
        email.setMsg(message);
        // add the attachment
        for (File file : attFile) {
            email.attach(file);
        }
        // send the email
        email.send();
    }

    /**
     * 发送一封带附件的邮件
     * @param subject 主题
     * @param message 内容
     * @param files 附件（本地资源）
     * @throws EmailException
     */
    public void sendAttachmentEmail(String subject, String message, List<File> files) throws EmailException {
        // Create the email message
        MultiPartEmail email = new MultiPartEmail();
        init(email,subject);
        email.setMsg(message);
        // add the attachment
        for (File file : files) {
            email.attach(file);
        }
        // send the email
        email.send();
    }

    /**
     * 发送一封带附件的邮件
     * @param subject 主题
     * @param message 内容
     * @param attachment 附件内容（本地资源和网络资源）
     * @throws EmailException
     */
    public void sendAttachmentEmail(String subject, String message, Attachment attachment) throws EmailException {
        // Create the email message
        MultiPartEmail email = new MultiPartEmail();
        init(email,subject);
        email.setMsg(message);
        List<File> fileList = attachment.getFileList();
        for (File file : fileList) {
            email.attach(file);
        }
        Map<String, URL> urlMap = attachment.getUrlMap();
        for(Map.Entry<String,URL> en:urlMap.entrySet()){
            email.attach(en.getValue(),en.getKey(),"");
        }
        email.send();
    }

    /**
     * 发送一封HTML格式的邮件
     * @param subject 主题
     * @param htmlMsg Html内容
     * @param alternativeMessage Html内容无法显示时替代的文本内容
     * @throws EmailException
     */
    public void sendHtmlEmail(String subject,HtmlMsg htmlMsg,String alternativeMessage) throws Exception {
        HtmlEmail email = new HtmlEmail();
        init(email,subject);
        HtmlTemp htmlTemp=new HtmlTemp(email);
        htmlMsg.setHtmlMsg(htmlTemp);
        email.setHtmlMsg(htmlTemp.getHtml());
        email.setTextMsg(alternativeMessage);
        email.send();
    }

    /**
     * 发送一封HTML格式的邮件
     * @param subject 主题
     * @param htmlMsg Html内容
     * @throws EmailException
     */
    public void sendHtmlEmail(String subject,HtmlMsg htmlMsg) throws Exception {
        sendHtmlEmail(subject,htmlMsg,"You have a new unread message...");
    }

    private Folder getReceiveFolder() throws Exception {
        //创建Session对象
        Properties prop=System.getProperties();
        Session session=Session.getDefaultInstance(prop);
        Store store=session.getStore("pop3");
        store.connect(emailConfig.getPopHost(),emailConfig.getEmail(),emailConfig.getPassword());
        Folder folder = store.getFolder("INBOX");
        return folder;
    }

    public int inboxSize() throws Exception {
        Folder folder = getReceiveFolder();
        folder.open(Folder.READ_ONLY);
        return folder.getMessageCount();
    }

    public int newMsgSize() throws Exception {
        Folder folder = getReceiveFolder();
        folder.open(Folder.READ_ONLY);
        return folder.getNewMessageCount();
    }

    public int delMsgSize() throws Exception {
        Folder folder = getReceiveFolder();
        folder.open(Folder.READ_ONLY);
        return folder.getDeletedMessageCount();
    }

    /**
     * 接收部分邮件
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<EmailContent> receiveEmailReadOnly(int start,int end) throws Exception{
        List<EmailContent> contents=new ArrayList<>();
        Folder folder =getReceiveFolder();
        folder.open(Folder.READ_ONLY);
        int messageCount = folder.getMessageCount();
        Message[] messages = folder.getMessages(start,end);
        for (Message message : messages) {
            contents.add(msgToEmailContent(message));
        }
        return contents;
    }

    /**
     * 接收所有邮件
     * @return
     * @throws Exception
     */
    public List<EmailContent> receiveAllEmailReadOnly() throws Exception {
        List<EmailContent> contents=new ArrayList<>();
        Folder folder =getReceiveFolder();
        folder.open(Folder.READ_ONLY);
        int messageCount = folder.getMessageCount();
        return receiveEmailReadOnly(1,messageCount);
    }

    private EmailContent msgToEmailContent(Message msg) throws Exception {
        MimeMessageParser parser=new MimeMessageParser((MimeMessage) msg);
        EmailContent emailContent=new EmailContent();
        emailContent.setSubject(parser.getSubject());
        emailContent.setFrom(parser.getFrom());
        emailContent.setSendDate(msg.getSentDate());
        emailContent.setType(msg.getContentType());
        emailContent.setTo(parser.getTo());
        emailContent.setCc(parser.getCc());
        emailContent.setBcc(parser.getBcc());
        if(parser.parse().hasPlainContent()){//文本内容
            emailContent.addTxtContent(parser.parse().getPlainContent());
        }
        if(parser.parse().hasHtmlContent()){//HTML内容
            emailContent.addHtmlContent(parser.parse().getHtmlContent());
        }
        if(parser.parse().hasAttachments()){//多文件
            emailContent.setFileContent(parser.parse().getAttachmentList());
        }
        return emailContent;
    }

}
