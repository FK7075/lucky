package com.lucky.web.mapping;

/**
 * 类的映射解析，将一个Controller分解为多个URL映射
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 9:51
 */
public interface MappingAnalysis {

    /**
     * 将一个Controller解析为多个URL映射
     * @param controller Controller对象
     * @return
     */
    MappingCollection analysis(Object controller);

}
