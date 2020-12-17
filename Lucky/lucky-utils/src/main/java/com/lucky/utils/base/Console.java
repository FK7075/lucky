package com.lucky.utils.base;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/10 9:30
 */
public abstract class Console {

    public static void println(Object object){
        System.out.println(object);
    }

    public static void print(Object object){
        System.out.print(object);
    }

    /**
     * 输出红色日志
     * @param object
     */
    public static void red(Object object){
        System.out.print("\033[1;31m"+object+"\033[0m");
    }

    /**
     * 返回红色字符
     * @param object
     * @return
     */
    public static String redStr(Object object){
        return "\033[1;31m"+object+"\033[0m";
    }

    /**
     * 输出青蓝色日志
     * @param object
     */
    public static void cyan(Object object){
        System.out.print("\033[1;36m"+object+"\033[0m");
    }

    /**
     * 返回青蓝色字符
     * @param object
     * @return
     */
    public static String cyanStr(Object object){
        return "\033[1;36m"+object+"\033[0m";
    }

    /**
     * 输出紫红色日志
     * @param object
     */
    public static void mulberry(Object object){
        System.out.print("\033[1;35m"+object+"\033[0m");
    }

    /**
     * 返回紫红色字符
     * @param object
     * @return
     */
    public static String mulberryStr(Object object){
        return "\033[1;35m"+object+"\033[0m";
    }

    /**
     * 输出黄色日志
     * @param object
     */
    public static void yellow(Object object){
        System.out.print("\033[1;33m"+object+"\033[0m");
    }

    /**
     * 返回黄色字符
     * @param object
     * @return
     */
    public static String yellowStr(Object object){
        return "\033[1;33m"+object+"\033[0m";
    }

    /**
     * 输出绿色日志
     * @param object
     */
    public static void green(Object object){
        System.out.print("\033[1;32m"+object+"\033[0m");
    }

    /**
     * 返回绿色字符
     * @param object
     * @return
     */
    public static String greenStr(Object object){
        return "\033[1;32m"+object+"\033[0m";
    }

    /**
     * 输出白色日志
     * @param object
     */
    public static void white(Object object){
        System.out.print("\033[1;37m"+object+"\033[0m");
    }

    /**
     * 返回白色字符
     * @param object
     * @return
     */
    public static String whiteStr(Object object){
        return "\033[1;37m"+object+"\033[0m";
    }

    /**
     * 输出蓝色日志
     * @param object
     */
    public static void blue(Object object){
        System.out.print("\033[1;34m"+object+"\033[0m");
    }

    /**
     * 返回蓝色字符
     * @param object
     * @return
     */
    public static String blueStr(Object object){
        return "\033[1;34m"+object+"\033[0m";
    }

    /**
     * 输出黑色日志
     * @param object
     */
    public static void black(Object object){
        System.out.print("\033[1;30m"+object+"\033[0m");
    }

    /**
     * 返回黑色字符
     * @param object
     * @return
     */
    public static String blackStr(Object object){
        return "\033[1;30m"+object+"\033[0m";
    }

    public static void main(String[] args) {
        red("---------红  色---------\n");
        cyan("---------青蓝色---------\n");
        mulberry("---------紫红色---------\n");
        black("---------黑  色---------\n");
        blue("---------蓝  色---------\n");
        white("---------白  色---------\n");
        yellow("---------黄  色---------\n");
        green("---------绿  色---------\n");

        //-------------------------------//
        println("\n-----------------------\n");
        //-------------------------------//

        println("---------"+redStr("红  色")+"---------");
        println("---------"+cyanStr("青蓝色")+"---------");
        println("---------"+mulberryStr("紫红色")+"---------");
        println("---------"+blackStr("黑  色")+"---------");
        println("---------"+blueStr("蓝  色")+"---------");
        println("---------"+whiteStr("白  色")+"---------");
        println("---------"+yellowStr("黄  色")+"---------");
        println("---------"+greenStr("绿  色")+"---------");

    }
}
