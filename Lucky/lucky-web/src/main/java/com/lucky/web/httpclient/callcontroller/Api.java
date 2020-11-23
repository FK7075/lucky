package com.lucky.web.httpclient.callcontroller;

import com.lucky.framework.expression.$Expression;
import com.lucky.web.conf.WebConfig;

import java.util.Map;


public class Api {

    private static final Map<String,String> callApi= WebConfig.getWebConfig().getCallApi();
    /**
     * 将注解中配置的CallApi转化为实际的地址
     * @param annApiStr
     * @return
     */
    public static String getApi(String annApiStr){
        if(annApiStr.startsWith("${")&&annApiStr.contains("}")){
            return $Expression.translation(annApiStr,callApi);
        }
        return annApiStr;
    }



}
