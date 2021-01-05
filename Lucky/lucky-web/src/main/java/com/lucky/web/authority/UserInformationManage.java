package com.lucky.web.authority;

import com.lucky.framework.container.Injection;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/3 下午9:27
 */
public abstract class UserInformationManage {

    private static UserInformation userInformation;

    public static UserInformation get() {
        if(userInformation!=null){
            Injection.injection(userInformation,"user-information");
        }
        return userInformation;
    }

    public static void set(UserInformation userInfo) {
        userInformation = userInfo;
    }

}
