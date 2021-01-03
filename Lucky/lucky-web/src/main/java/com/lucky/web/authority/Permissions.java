package com.lucky.web.authority;

import com.lucky.utils.base.ArrayUtils;

import java.util.Set;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/4 上午12:49
 */
public class Permissions {

    private Logical logical;
    private Set<String> permissions;

    public Permissions() {}

    public Permissions(Logical logical, String[] permissions) {
        this.logical = logical;
        this.permissions = ArrayUtils.arrayToSet(permissions);
    }

    public Logical getLogical() {
        return logical;
    }

    public void setLogical(Logical logical) {
        this.logical = logical;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public boolean check(Set<String> userPermissions){
        if(logical==null){
            return true;
        }
        if(userPermissions==null){
            return false;
        }
        if(logical==Logical.AND){
            return permissions.containsAll(userPermissions);
        }
        for (String userPermission : userPermissions) {
            if(permissions.contains(userPermission)){
                return true;
            }
        }
        return false;
    }
}
