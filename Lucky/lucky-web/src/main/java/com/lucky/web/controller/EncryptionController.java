package com.lucky.web.controller;

import com.lucky.utils.dm5.MD5Utils;

/**
 * 加密解密相关操作的Controller基类
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/3 上午2:14
 */
public abstract class EncryptionController extends LuckyController{

    /**
     * MD5加密
     * @param clear 明文
     * @return 密文
     */
    protected String md5(String clear){
        return MD5Utils.md5(clear);
    }

    /**
     * MD5加密
     * @param clear 明文
     * @param salt 盐
     * @param cycle 循环加密的次数
     * @return
     */
    protected String md5(String clear,String salt,int cycle){
        return MD5Utils.md5(clear,salt,cycle);
    }
}
