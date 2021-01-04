package com.lucky.web.authority;

import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;

import java.util.Set;

/**
 * 资源封装器
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/4 上午12:49
 */
public class Permissions {

    /** 处理逻辑*/
    private Logical logical;
    /** 访问该资源必须要拥有的权限*/
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

    /**
     * 资源权限校验
     * @param userPermissions 当前用户所拥有的所有权限
     * @return 验证通过返回true，否则返回false
     */
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
