package com.lucky.web.authority;

import com.lucky.utils.base.ArrayUtils;
import com.lucky.utils.base.Assert;

import java.util.Set;

/**
 * 角色封装器
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/4 上午12:48
 */
public class Role {

    /** 处理逻辑*/
    private Logical logical;
    /** 可以访问该资源的角色*/
    private Set<String> roles;

    public Role(){};

    public Role(String[] roles,Logical logical){
        this.logical=logical;
        this.roles= ArrayUtils.arrayToSet(roles);
    }

    public Logical getLogical() {
        return logical;
    }

    public void setLogical(Logical logical) {
        this.logical = logical;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * 角色校验
     * @param userRoles 当前用户所拥有的所有角色
     * @return 验证通过返回true，否则返回false
     */
    public boolean check(Set<String> userRoles){
        if(logical==null){
            return true;
        }
        if(Assert.isEmptyCollection(userRoles)){
            return false;
        }
        if(logical==Logical.AND){
           return userRoles.containsAll(roles);
        }
        for (String role : roles) {
            if(userRoles.contains(role)){
                return true;
            }
        }
        return false;
    }
}
