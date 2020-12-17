package com.lucky.framework.welcome;

import com.lucky.framework.exception.LuckyIOException;
import com.lucky.utils.base.Console;
import com.lucky.utils.file.Resources;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public abstract class JackLamb {

    private static final Logger log= LoggerFactory.getLogger(JackLamb.class);
    public final static String USER_LOGO_FILE= "/conf/logo.luck";
    public final static String DEFAULT_LOGO_FILE= "/lucky-framework/lucky.luck";
    public static boolean first = true;


    /**
     * 打印logo
     */
    public static void welcome() {
        if (!first)
            return;
        InputStream logoStream = Resources.getInputStream(USER_LOGO_FILE);
        if (logoStream != null) {
            print(USER_LOGO_FILE);
        }
        print(DEFAULT_LOGO_FILE);
    }

    /**
     * 打印LOGO
     * @param filePath LOGO的文件位置
     */
    private static void print(String filePath){
        try {
            BufferedReader reader = Resources.getReader(DEFAULT_LOGO_FILE);
            Console.white(IOUtils.toString(reader));
            versionInfo();
            first = false;
        } catch (IOException e) {
           throw new LuckyIOException(e);
        }
    }


    private static int getMaxLength(String os, String java, String lucky) {
        int os_l = os.length();
        int java_l = java.length();
        int lucky_l = lucky.length();
        int temp = os_l > java_l ? os_l : java_l;
        return temp > lucky_l ? temp : lucky_l;
    }

    private static String getSameStr(String str, int maxLength) {
        if (str.length() == maxLength) {
            return str;
        }
        int poor = maxLength - str.length();
        for (int i = 0; i < poor; i++)
            str += " ";
        return str;
    }

    /**
     * 获取版本信息(OS,Java,Lucky)
     * @return
     */
    public static void versionInfo() {
        String os = ":: " + System.getProperty("os.name");
        String osvsersion = "           :: (v" + System.getProperty("os.version") + ")";
        String java = ":: Java";
        String javaversioin = "           :: (v" + System.getProperty("java.version") + ")";
        String lucky = ":: Lucky";
        String luckyversion = "           :: ("+Version.version()+")";
        int maxLength = getMaxLength(os, java, lucky);
        String d = "";
        Console.print("\n\n    ");Console.white( getSameStr(java, maxLength));Console.white(javaversioin);
        Console.print("\n    ");Console.white( getSameStr(lucky, maxLength));Console.white(luckyversion);
        Console.green("\n    ");Console.white( getSameStr(os, maxLength));Console.white(osvsersion+"\n\n");
    }
}
