package com.lucky.utils.test.jexl;

import org.apache.commons.jexl3.*;
import org.apache.commons.jexl3.internal.Engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/21 0021 9:21
 */
public class JEXLTest {

    public static void main(String[] args) {
        JexlEngine engine = new Engine();//创建表达式引擎对象
        JexlContext context = new MapContext();//创建Context设值对象
        String expressionStr = "array.get(0)";//表达式，表达式可以是数组的属性，元素等
        List<Object> array = new ArrayList<Object>();//创建一个列表
        array.add("this is an array");
        array.add(new Integer(2));
        context.set("array",array);//使用context对象将表达式中用到的值设置进去，必须是所有用到的值
        JexlExpression expression = engine.createExpression(expressionStr);
        Object o = expression.evaluate(context);//使用表达式对象开始计算
        System.out.println(o);//输出：2
    }

}
