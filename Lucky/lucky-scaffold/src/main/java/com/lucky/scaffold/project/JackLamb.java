package com.lucky.scaffold.project;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/24 13:09
 */
public abstract class JackLamb {

    private static final String logoStream="                                                                                           \n" +
            ",--.                 ,--.              ,---.                 ,---. ,---.       ,--.   ,--. \n" +
            "|  |   ,--.,--. ,---.|  |,-. ,--. ,--.'   .-'  ,---. ,--,--./  .-'/  .-' ,---. |  | ,-|  | \n" +
            "|  |   |  ||  || .--'|     /  \\  '  / `.  `-. | .--'' ,-.  ||  `-,|  `-,| .-. ||  |' .-. | \n" +
            "|  '--.'  ''  '\\ `--.|  \\  \\   \\   '  .-'    |\\ `--.\\ '-'  ||  .-'|  .-'' '-' '|  |\\ `-' | \n" +
            "`-----' `----'  `---'`--'`--'.-'  /   `-----'  `---' `--`--'`--'  `--'   `---' `--' `---'  \n" +
            "                             `---'                                                         ";

    /**
     * 打印logo
     */
    public static void welcome() {
        System.out.println(logoStream);
        System.out.println(versionInfo());
    }

    /**
     * 获取版本信息(OS,Java,Lucky)
     * @return
     */
    public static String versionInfo(){
        String os = ":: " + System.getProperty("os.name");
        String osvsersion = "           :: (v" + System.getProperty("os.version") + ")";
        String java = ":: Java";
        String javaversioin = "           :: (v" + System.getProperty("java.version") + ")";
        String lucky = ":: Lucky Scaffold";
        String luckyversion = "           :: (v1.1.1)";
        int maxLength = getMaxLength(os, java, lucky);
        String d="";
        d += "\n\t\t" + getSameStr(os, maxLength) + osvsersion;
        d += "\n\t\t" + getSameStr(java, maxLength) + javaversioin;
        d += "\n\t\t" + getSameStr(lucky, maxLength) + luckyversion+"\n\n";
        return d;
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
}
