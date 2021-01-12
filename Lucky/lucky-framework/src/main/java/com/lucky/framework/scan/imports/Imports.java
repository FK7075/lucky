package com.lucky.framework.scan.imports;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/12 0012 17:04
 */
public interface Imports {

    default Class<?>[] imports(){
        return new Class[0];
    }
}
