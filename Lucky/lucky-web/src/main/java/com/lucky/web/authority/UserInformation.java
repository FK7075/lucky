package com.lucky.web.authority;

/**
 * 用户信息
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/3 下午4:03
 */
public interface UserInformation {

    /**
     * 是否已经验证(登录)
     * @return
     */
    boolean isAuthenticated();

    /**
     * 用户的角色和拥有的权限信息
     * @return
     */
    default RoleAndPermissions roleAndPermissions(){
        return new RoleAndPermissions();
    }

    /**
     * 获取用户的关键信息
     * @return
     */
    Object getUser();
}
