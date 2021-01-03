package com.lucky.web.authority;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/3 下午9:27
 */
public class UserInformationManage {

    private static UserInformationManage manage;
    private UserInformation userInformation;

    public UserInformation getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }

    private UserInformationManage(){};

    public static UserInformationManage create(){
        if(manage==null){
            manage=new UserInformationManage();
        }
        return manage;
    }
}
