package com.lucky.web.error;

import com.lucky.framework.uitls.base.BaseUtils;
import com.lucky.framework.uitls.file.Resources;
import com.lucky.framework.exception.LuckyIOException;
import com.lucky.web.conf.WebConfig;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 错误页面
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 14:08
 */
public abstract class ErrorPage {

    private final static String LUCKY_STR="LUCKY_RDS_CHAR_DOLLAR_\\)&@!";
    private static Map<String,String> errorPageMap;

    static {
        errorPageMap=new HashMap<>();
        Map<String, String> errPagePathMap = WebConfig.getWebConfig().getErrorPage();
        for(Map.Entry<String,String> entry:errPagePathMap.entrySet()){
            errorPageMap.put(entry.getKey(),getHtmlString(entry.getValue()));
        }
    }

    /**
     * 使用错误信息填充对应的错误页面,并返回完整的HTML代码
     * @param errorCode 错误代码[404,500,403...]
     * @param errorMessage 错误描述
     * @param errorDescription 错误的具体说明
     * @return 错误页面的完整HTML代码
     */
    public static String exception(String errorCode,String errorMessage, String errorDescription){
        String errorPageHtml=errorPageMap.get(errorCode);
        errorPageHtml = errorPageHtml.replaceAll("@:errType", errorCode);
        errorPageHtml = errorPageHtml.replaceAll("@:time", BaseUtils.time());
        errorPageHtml = errorPageHtml.replaceAll("@:Message", errorMessage.replaceAll("\\$", LUCKY_STR));
        errorPageHtml = errorPageHtml.replaceAll("@:Description", errorDescription.replaceAll("\\$", LUCKY_STR));
        errorPageHtml=errorPageHtml.replaceAll( LUCKY_STR,"\\$");
        return errorPageHtml;
    }

    /**
     * 使用错误信息填充404错误页面,并返回完整的HTML代码
     * @param errorMessage 错误描述
     * @param errorDescription 错误的具体说明
     * @return
     */
    public static String get404Html(String errorMessage, String errorDescription){
        return exception("404",errorMessage,errorDescription);
    }

    /**
     * 使用错误信息填充403错误页面,并返回完整的HTML代码
     * @param errorMessage 错误描述
     * @param errorDescription 错误的具体说明
     * @return
     */
    public static String get403Html(String errorMessage, String errorDescription){
        return exception("403",errorMessage,errorDescription);
    }

    /**
     * 使用错误信息填充500错误页面,并返回完整的HTML代码
     * @param errorMessage 错误描述
     * @param errorDescription 错误的具体说明
     * @return
     */
    public static String get500Html(String errorMessage, String errorDescription){
        return exception("500",errorMessage,errorDescription);
    }



    private static String getHtmlString(String path) {
        StringWriter sw=new StringWriter();
        BufferedReader reader = Resources.getReader(path);
        try {
            IOUtils.copy(reader,sw);
            return sw.toString();
        }catch (IOException e){
            throw new LuckyIOException(e);
        }
    }
}
