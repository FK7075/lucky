package com.lucky.web.authority;

import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;

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

    public Permissions(String[] permissions,Logical logical) {
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
        if(Assert.isEmptyCollection(userPermissions)){
            return false;
        }
        if(logical==Logical.AND){
            return userPermissions.containsAll(permissions);
        }
        for (String permission : permissions) {
            if(userPermissions.contains(permission)){
                return true;
            }
        }
        return false;
    }
}
