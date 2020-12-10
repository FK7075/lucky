package com.lucky.email.core;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import javax.activation.DataSource;
import java.io.File;
import java.net.URL;

/**
 * HTML模板，用于编辑保存一套固定的HTML邮件
 *
 * @author fk7075
 * @version 1.0
 * @date 2020/9/8 10:44
 */
public class HtmlTemp {

    /**
     * Apache Commons HtmlEmail
     */
    private HtmlEmail htmlEmail;

    /**
     * 最终的HTML代码
     */
    private StringBuilder html;

    /**
     * 固定前缀
     */
    private String prefix;

    /**
     * 固定后缀
     */
    private String suffix;

    public HtmlTemp(HtmlEmail htmlEmail) {
        this.htmlEmail = htmlEmail;
        html = new StringBuilder();
    }

    /**
     * 设置一个固定的HTML前缀
     *
     * @param prefix 前缀模板代码
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 设置一个固定的HTML后缀
     *
     * @param suffix 后缀模板代码
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * 将URL解析为CID
     *
     * @param url  URL
     * @param name 文件名
     * @return CID
     * @throws EmailException
     */
    public String embed(URL url, String name) throws EmailException {
        return htmlEmail.embed(url, name);
    }

    /**
     * 将本地文件解析为CID
     *
     * @param file 本地文件
     * @param cid  CID
     * @return CID
     * @throws EmailException
     */
    public String embed(File file, String cid) throws EmailException {
        return htmlEmail.embed(file, cid);
    }

    /**
     * 将本地文件解析为CID
     *
     * @param file 本地文件
     * @return CID
     * @throws EmailException
     */
    public String embed(File file) throws EmailException {
        return htmlEmail.embed(file);
    }

    /**
     * 将网络文件解析为CID
     *
     * @param urlString 网络资源的URL地址
     * @param name      文件名
     * @return CID
     * @throws EmailException
     */
    public String embed(String urlString, String name) throws EmailException {
        return htmlEmail.embed(urlString, name);
    }

    /**
     * 将DataSource解析为CID
     *
     * @param dataSource DataSource
     * @param name       文件名
     * @return CID
     * @throws EmailException
     */
    public String embed(DataSource dataSource, String name) throws EmailException {
        return htmlEmail.embed(dataSource, name);
    }

    /**
     * 将DataSource解析为CID
     *
     * @param dataSource DataSource
     * @param name       文件名
     * @param cid        CID
     * @return CID
     * @throws EmailException
     */
    public String embed(DataSource dataSource, String name, String cid) throws EmailException {
        return htmlEmail.embed(dataSource, name, cid);
    }

    /**
     * 拼接一段HTML代码
     *
     * @param html   HTML代码片段
     * @param params 动态参数，用法参照String.format()方法
     * @return
     */
    public HtmlTemp addHtml(String html, String... params) {
        this.html.append(String.format(html, params));
        return this;
    }

    /**
     * 拼接一段有文件嵌入的HTML代码
     *
     * @param htmlFragment HTML代码片段
     * @param files        动态文件参数，用法参照String.format()方法
     * @return
     * @throws EmailException
     */
    public HtmlTemp addHtmlFile(String htmlFragment, File... files) throws EmailException {
        String[] cids = new String[files.length];
        for (int i = 0, j = files.length; i < j; i++) {
            cids[i] = htmlEmail.embed(files[i]);
        }
        html.append(String.format(htmlFragment, cids));
        return this;
    }

    /**
     * 拼接一段有文件嵌入的HTML代码
     *
     * @param htmlFragment HTML代码片段
     * @param resources    动态文件参数，用法参照String.format()方法
     * @return
     * @throws EmailException
     */
    public HtmlTemp addHtmlFile(String htmlFragment, Resource<File>... resources) throws EmailException {
        String[] cids = new String[resources.length];
        for (int i = 0, j = resources.length; i < j; i++) {
            cids[i] = htmlEmail.embed(resources[i].getResource(), resources[i].getName());
        }
        html.append(String.format(htmlFragment, cids));
        return this;
    }

    /**
     * 拼接一段有网络文件嵌入的HTML代码
     *
     * @param htmlFragment HTML代码片段
     * @param urlString    文件地址，用法参照String.format()方法
     * @return
     */
    public HtmlTemp addHtmlURL(String htmlFragment, String... urlString) {
        html.append(String.format(htmlFragment, urlString));
        return this;
    }

    /**
     * 拼接一段有网络文件嵌入的HTML代码
     *
     * @param htmlFragment HTML代码片段
     * @param resources    文件URL和文件名，用法参照String.format()方法
     * @return
     */
    public HtmlTemp addHtmlURL(String htmlFragment, Resource<URL>... resources) throws EmailException {
        String[] cids = new String[resources.length];
        for (int i = 0, j = resources.length; i < j; i++) {
            cids[i] = htmlEmail.embed(resources[i].getResource(), resources[i].getName());
        }
        html.append(String.format(htmlFragment, cids));
        return this;
    }

    /**
     * 拼接一段有网络文件嵌入的HTML代码
     *
     * @param htmlFragment HTML代码片段
     * @param resources    文件地址和文件名，用法参照String.format()方法
     * @return
     */
    public HtmlTemp addHtmlStrURL(String htmlFragment, Resource<String>... resources) throws EmailException {
        String[] cids = new String[resources.length];
        for (int i = 0, j = resources.length; i < j; i++) {
            cids[i] = htmlEmail.embed(resources[i].getResource(), resources[i].getName());
        }
        html.append(String.format(htmlFragment, cids));
        return this;
    }

    /**
     * 拼接一段有网络文件嵌入的HTML代码
     *
     * @param htmlFragment HTML代码片段
     * @param resources    DataSource和文件名，用法参照String.format()方法
     * @return
     * @throws EmailException
     */
    public HtmlTemp addHtmlDataSource(String htmlFragment, Resource<DataSource>... resources) throws EmailException {
        String[] cids = new String[resources.length];
        for (int i = 0, j = resources.length; i < j; i++) {
            cids[i] = htmlEmail.embed(resources[i].getResource(), resources[i].getName());
        }
        html.append(String.format(htmlFragment, cids));
        return this;
    }

    public String getHtml() {
        if (prefix != null) {
            html = new StringBuilder(prefix).append(html);
        }
        if (suffix != null) {
            html.append(suffix);
        }
        return html.toString();
    }


}
