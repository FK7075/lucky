package com.lucky.framework.scan.exclusions;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/12 0012 17:03
 */
public interface Exclusions {

    default Class<?>[] exclusions(){
        return new Class[0];
    }
}
