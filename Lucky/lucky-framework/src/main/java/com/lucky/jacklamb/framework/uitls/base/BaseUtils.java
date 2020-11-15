package com.lucky.jacklamb.framework.uitls.base;

/**
 * 基本工具类
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 1:56 下午
 */
public abstract class BaseUtils {

    /**
     * 单词的首字母大写
     * @param word 原始单词
     * @return 首字母变大写后的单词
     */
    public static String capitalizeTheFirstLetter(String word) {
        return word.toUpperCase().substring(0, 1)+word.substring(1, word.length());
    }

    /**
     * 单词的首字母小写
     * @param word 原始单词
     * @return 首字母变小写后的单词
     */
    public static String lowercaseFirstLetter(String word) {
        return word.toLowerCase().substring(0, 1)+word.substring(1, word.length());
    }
}
