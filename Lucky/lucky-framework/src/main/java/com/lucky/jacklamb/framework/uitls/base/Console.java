package com.lucky.jacklamb.framework.uitls.base;

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
     * 输出青蓝色日志
     * @param object
     */
    public static void cyan(Object object){
        System.out.print("\033[1;36m"+object+"\033[0m");
    }

    /**
     * 输出紫红色日志
     * @param object
     */
    public static void mulberry(Object object){
        System.out.print("\033[1;35m"+object+"\033[0m");
    }

    /**
     * 输出黄色日志
     * @param object
     */
    public static void yellow(Object object){
        System.out.print("\033[1;33m"+object+"\033[0m");
    }

    /**
     * 输出绿色日志
     * @param object
     */
    public static void green(Object object){
        System.out.print("\033[1;32m"+object+"\033[0m");
    }

    /**
     * 输出白色日志
     * @param object
     */
    public static void white(Object object){
        System.out.print("\033[1;37m"+object+"\033[0m");
    }

    /**
     * 输出蓝色日志
     * @param object
     */
    public static void blue(Object object){
        System.out.print("\033[1;34m"+object+"\033[0m");
    }

    /**
     * 输出黑色日志
     * @param object
     */
    public static void black(Object object){
        System.out.print("\033[1;30m"+object+"\033[0m");
    }

    public static void main(String[] args) {
        red("红色\n");
        cyan("青蓝色\n");
        mulberry("紫红色\n");
        black("黑色\n");
        blue("蓝色\n");
        white("白色\n");
        yellow("黄色\n");
        green("绿色\n");

    }
}
