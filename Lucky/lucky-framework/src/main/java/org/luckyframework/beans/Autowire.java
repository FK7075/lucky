package org.luckyframework.beans;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/22 上午1:41
 */
public enum Autowire {


    /**
     * Constant that indicates no autowiring at all.
     */
    NO,

    /**
     * Constant that indicates autowiring bean properties by name.
     */
    BY_NAME,

    /**
     * Constant that indicates autowiring bean properties by type.
     */
    BY_TYPE;
}
