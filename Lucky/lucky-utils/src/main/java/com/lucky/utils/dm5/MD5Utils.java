package com.lucky.utils.dm5;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    /**
     * MD5加密
     * @param clear 明文
     * @param salt 盐
     * @param cycle 循环执行加密算法次数
     * @param capital 是否每次执行后是否将英文改为大写
     * @return
     */
    public static String md5(String clear,String salt,int cycle, boolean capital){
        for(int i=0;i<cycle;i++){
            clear=capital? DigestUtils.md5Hex(salt+clear+salt).toUpperCase():DigestUtils.md5Hex(salt+clear+salt);
        }
        return clear;
    }

    /**
     * MD5加密，每次加密完成后将密文都转化为大写
     * @param clear 明文
     * @param salt 盐
     * @param cycle 循环执行加密算法次数
     * @return
     */
    public static String md5UpperCase(String clear,String salt,int cycle){
        return md5(clear,salt,cycle,true);
    }

    /**
     * MD5加密
     * @param clear 明文
     * @param salt 盐
     * @param cycle 循环执行加密算法次数
     * @return
     */
    public static String md5(String clear,String salt,int cycle){
        return md5(clear,salt,cycle,false);
    }

    /**
     * MD5加密
     * @param clear
     * @return
     */
    public static String md5(String clear){
        return DigestUtils.md5Hex(clear);
    }

}
