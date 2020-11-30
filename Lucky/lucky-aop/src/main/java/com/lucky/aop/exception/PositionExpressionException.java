package com.lucky.aop.exception;

import java.lang.reflect.Method;

/**
 * 定位表达式异常
 * @author fk
 * @version 1.0
 * @date 2020/11/30 0030 8:54
 */
public class PositionExpressionException extends RuntimeException{

    public PositionExpressionException(Method method,String positionExpressionp){
        super(String.format("AOP定位表达式异常！错误的AOP定位表达式: `%s` , 错误位置: %s ！正确的书写格式为：P:{包检验表达式}C:{N[类名检验表达式],I[IOC_ID校验表达式],T[IOC_TYPE校验表达式],A[是否被注解]}{N[方法名校验表达式],A[是否被注解],AC[访问修饰符],O[要增强的继承自Object对象的方法]}",positionExpressionp,method));
    }
}
