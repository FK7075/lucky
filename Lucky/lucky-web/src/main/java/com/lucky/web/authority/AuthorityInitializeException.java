package com.lucky.web.authority;

import java.lang.reflect.Method;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/4 上午2:02
 */
public class AuthorityInitializeException extends RuntimeException {

    public AuthorityInitializeException(Method method){
        super("权限管理初始化异常！Controller方法不可同时被「 @MustGuest 」和「 @MustPermissions、@MustRoles、@MustUser 」注解标注！错误位置："+method);
    }
}
