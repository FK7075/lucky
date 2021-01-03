package com.lucky.web.authority;

import com.lucky.utils.base.ArrayUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色和权限
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/4 上午4:33
 */
public class RoleAndPermissions {

    /** 当前用户对应的角色*/
    private Set<String> roles;
    /** 当前用户对应的权限*/
    private Set<String> permissions;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void addRole(String...role){
        if(roles==null){
            roles=new HashSet<>();
        }
        roles.addAll(ArrayUtils.arrayToSet(role));
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public void addPermission(String...permission){
        if(permissions==null){
            permissions=new HashSet();
        }
        permissions.addAll(ArrayUtils.arrayToSet(permission));
    }
}
