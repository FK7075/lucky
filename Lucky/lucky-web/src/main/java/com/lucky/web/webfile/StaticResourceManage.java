package com.lucky.web.webfile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lucky.utils.base.Assert;
import com.lucky.utils.file.Resources;
import com.lucky.web.conf.WebConfig;
import com.lucky.web.core.Model;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/19 17:48
 */
public class StaticResourceManage {

    private static Map<String,String> contentTypeMap;
    private static final String WebRoot= WebConfig.getWebConfig().getWebRoot();
    private static String WEB_ROOT_PREFIX;
    private static String TARGET_WEB_ROOT;

    static{
        contentTypeMap=new HashMap<>();
        BufferedReader br = Resources.getReader("/lucky-web/content-type.json");
        Type type=new TypeToken<List<String[]>>(){}.getType();
        List<String[]> arrContentType=new Gson().fromJson(br,type);
        for (String[] kv : arrContentType) {
            contentTypeMap.put(kv[0],kv[1]);
        }
        String webRoot=WebRoot;
        if(webRoot.startsWith("classpath:")){
            webRoot=webRoot.substring(10);
            webRoot=webRoot.startsWith("/")?webRoot:"/"+webRoot;
            TARGET_WEB_ROOT=webRoot.endsWith("/")?webRoot.substring(0,webRoot.length()-1):webRoot;
            WEB_ROOT_PREFIX="CP";
        }else if(webRoot.startsWith("user.dir:")){
            webRoot=webRoot.substring(9);
            webRoot=webRoot.startsWith("/")?webRoot:"/"+webRoot;
            webRoot=webRoot.endsWith("/")?webRoot.substring(0,webRoot.length()-1):webRoot;
            TARGET_WEB_ROOT=System.getProperty("user.dir") + webRoot;
            WEB_ROOT_PREFIX="UD";
        }else if(webRoot.startsWith("docBase:")){
            TARGET_WEB_ROOT=webRoot.substring(8);
            WEB_ROOT_PREFIX="DB";
        }else{
            TARGET_WEB_ROOT=webRoot.endsWith("/")?webRoot.substring(0,webRoot.length()-1):webRoot;
            WEB_ROOT_PREFIX="ABS";
        }
    }

    public static boolean isLegalIp(WebConfig webCfg, String currIp) {
        if (!webCfg.getStaticResourcesIpRestrict().isEmpty() && !webCfg.getStaticResourcesIpRestrict().contains(currIp)) {
            return false;
        }
        return true;
    }

    public static boolean isLegalRequest(WebConfig webCfg, String currIp, HttpServletResponse resp, String uri) {
        return isLegalIp(webCfg, currIp) && isStaticResource(resp, uri);
    }

    public static boolean isStaticResource(HttpServletResponse resp, String uri) {
        if(!uri.contains(".")) {
            return false;
        }
        String lowercaseUri = uri.toLowerCase();
        lowercaseUri=lowercaseUri.substring(lowercaseUri.lastIndexOf("."));
        if(contentTypeMap.containsKey(lowercaseUri)){
            resp.setContentType(contentTypeMap.get(lowercaseUri));
            return true;
        }
        return false;
    }

    public static boolean resources(Model model, String uri){
        //uri /xxx/xxx
        if(docBaseFileIsExist(model,uri)) {
            return true;
        }
        switch (WEB_ROOT_PREFIX){
            case "CP" :return Assert.isNotNull(Resources.getInputStream(TARGET_WEB_ROOT+uri));
            case "DB" :return model.getRealFile(TARGET_WEB_ROOT+uri)!=null;
            default   :return new File(TARGET_WEB_ROOT+uri).exists();
        }
    }

    private static boolean docBaseFileIsExist(Model model, String uri){
        if(model.docBaseIsExist()){
            return model.getRealFile(uri).exists();
        }
        return false;
    }

    public static void response(Model model, String uri) throws IOException {
        if(docBaseFileIsExist(model,uri)){
            WebFileUtils.preview(model, model.getRealFile(uri));
            return;
        }
        switch (WEB_ROOT_PREFIX){
            case "CP" :{
                InputStream staticStream=Resources.getInputStream(TARGET_WEB_ROOT+uri);
                WebFileUtils.preview(model,staticStream,uri.substring(uri.lastIndexOf("/")));
                break;
            }
            case "DB" :{
                File staticFile=model.getRealFile(TARGET_WEB_ROOT+uri);
                WebFileUtils.preview(model, staticFile);
                break;
            }
            default: {
                File staticFile=new File(TARGET_WEB_ROOT+uri);
                WebFileUtils.preview(model, staticFile);
                break;
            }
        }
    }

}
