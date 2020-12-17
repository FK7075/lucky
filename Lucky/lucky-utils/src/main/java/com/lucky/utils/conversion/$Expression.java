package com.lucky.utils.conversion;

import com.lucky.utils.base.Assert;
import com.lucky.utils.regula.Regular;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class $Expression {

    public static String translation(String original, Map<String,String> source){
        if(!original.contains("${")||!original.contains("}"))
            return original;
        if(Assert.isEmptyMap(source)){
            throw new ExpressionParsingException("表达式 `"+original+"` 解析异常：空的 `源集合` !");
        }
        List<String> $_key= Regular.getArrayByExpression(original.trim(),Regular.$_$);
        List<String> key=$_key.stream().map(a->a.substring(2,a.length()-1)).collect(Collectors.toList());
        for(int i=0;i<$_key.size();i++){
            if(source.containsKey(key.get(i))){
                original=original.replace($_key.get(i),source.get(key.get(i)));
                continue;
            }
            throw new ExpressionParsingException("表达式解析异常！在 `源集合` 中不存key为：`"+key.get(i)+"` 的解析数据");
        }
        return original;
    }

    public static String translationSharp(String original, Map<String,Object> source){
        if(!original.contains("#{")||!original.contains("}"))
            return original;
        if(Assert.isEmptyMap(source)){
            throw new ExpressionParsingException("表达式 `"+original+"` 解析异常：空的 `源集合` !");
        }
        List<String> $_key= Regular.getArrayByExpression(original.trim(),Regular.Sharp);
        List<String> key=$_key.stream().map(a->a.substring(2,a.length()-1)).collect(Collectors.toList());
        for(int i=0;i<$_key.size();i++){
            if(source.containsKey(key.get(i))) {
                original = original.replace($_key.get(i), source.get(key.get(i)).toString());
            }
            throw new ExpressionParsingException("表达式解析异常！在 `源集合` 中不存key为：`"+key.get(i)+"` 的解析数据");
        }
        return original;
    }
}
