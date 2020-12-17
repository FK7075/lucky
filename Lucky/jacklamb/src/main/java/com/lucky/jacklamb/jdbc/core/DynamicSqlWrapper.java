package com.lucky.jacklamb.jdbc.core;

/**
 * 动态SQL包装器，用于产生一个动态的SQl片段
 * @author fk7075
 * @version 1.0
 * @date 2020/8/19 10:20
 */
@FunctionalInterface
public interface DynamicSqlWrapper {

    /**
     * 产生一个动态的SQl片段
     * @param sp 规则定义器,定义生成动态SQL片段的规则
     */
    void dySql(SplicingRules sp);

}
