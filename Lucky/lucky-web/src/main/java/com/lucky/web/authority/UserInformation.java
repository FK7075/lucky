package com.lucky.web.authority;

import java.util.HashSet;
import java.util.Set;

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
     * 返回用户的角色信息
     * @return
     */
    default Set<String> getUserRoles(){
        return null;
    }

    /**
     * 返回用户的权限信息
     * @return
     */
    default Set<String> getUserPermissions(){
        return null;
    }
}
