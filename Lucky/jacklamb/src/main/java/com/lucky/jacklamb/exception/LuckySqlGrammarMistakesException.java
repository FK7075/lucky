package com.lucky.jacklamb.exception;

import java.lang.reflect.Method;

public class LuckySqlGrammarMistakesException extends RuntimeException {

    public LuckySqlGrammarMistakesException(String errSql, int index){
        super("错误的预编译SQl,SQL中的参数描述错误！索引超过参数列表的范围！ERROR-index: [?"+index+"] , ERROR-SQL："+errSql);
    }

    public LuckySqlGrammarMistakesException(String message){
        super(message);
    }

    public LuckySqlGrammarMistakesException(Method method, String errsql, String name){
        super("错误的预编译SQl,SQL中的参数描述错误！找不到可以匹配的参数[@:"+name+"], SQL: "+errsql+" , Method:"+method);
    }

    public LuckySqlGrammarMistakesException(Method method, Throwable e){
        super("获取方法参数列表时出现异常！Method: "+method,e);
    }

}
