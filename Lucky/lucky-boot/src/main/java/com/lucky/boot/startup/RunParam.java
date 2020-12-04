package com.lucky.boot.startup;

import com.lucky.boot.conf.ServerConfig;
import com.lucky.framework.confanalysis.ConfigUtils;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.conversion.JavaConversion;

import java.util.Arrays;
import java.util.List;

public abstract class RunParam {

    public static final String SERVER_PORT="server.port";

    public static final String LUCKY_CONFIG_LOCATION= ConfigUtils.LUCKY_CONFIG_LOCATION;

    public static List<String> params;

    static{
        String[] p={SERVER_PORT,LUCKY_CONFIG_LOCATION};
        params=Arrays.asList(p);
    }

    public static boolean isRunParam(String paramName){
       return params.contains(paramName);
    }

    public static ServerConfig withConf(String[] args ){
        for (String arg : args) {
            String[] mainKV = arg.split("=");
            if(isRunParam(mainKV[0].trim())){
                System.setProperty(mainKV[0].trim(),mainKV[1]);
            }
        }
        ServerConfig serverConfig=ServerConfig.getServerConfig();
        String runPort = System.getProperty(SERVER_PORT);
        if(Assert.isNotNull(runPort)){
            serverConfig.setPort((Integer) JavaConversion.strToBasic(runPort,int.class));
        }
        return serverConfig;
    }


}
