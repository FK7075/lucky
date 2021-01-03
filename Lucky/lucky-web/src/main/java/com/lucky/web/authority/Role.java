package com.lucky.web.authority;

import com.lucky.utils.base.ArrayUtils;

import java.util.Set;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/1/4 上午12:48
 */
public class Role {

    private Logical logical;
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

    public boolean check(Set<String> userRoles){
        if(logical==null){
            return true;
        }
        if(userRoles==null){
            return false;
        }
        if(logical==Logical.AND){
           return roles.containsAll(userRoles);
        }
        for (String userRole : userRoles) {
            if(roles.contains(userRole)){
                return true;
            }
        }
        return false;
    }
}
