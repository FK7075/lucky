package com.lucky.jacklamb.enums;

/**
 * 主键类型
 * @author fk
 * @version 1.0
 * @date 2020/12/17 0017 14:18
 */
public enum PrimaryType {

    /**
     * 自动增长的UUID主键
     */
    AUTO_UUID,
    /**
     * 自动增长的INT主键
     */
    AUTO_INT,
    /**
     * 非自动增长的主键
     */
    DEFAULT
}
