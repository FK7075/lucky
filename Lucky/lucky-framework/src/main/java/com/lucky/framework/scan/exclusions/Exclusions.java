package com.lucky.framework.scan.exclusions;

/**
 * 用于指定一系列需要排除的组件，这些指定的组件将不会参与组件扫描
 * @author fk
 * @version 1.0
 * @date 2021/1/12 0012 17:03
 */
public interface Exclusions {

    default Class<?>[] exclusions(){
        return new Class[0];
    }
}
