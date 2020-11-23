package com.lucky.framework.expression;

import com.lucky.framework.uitls.regula.Regular;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class $Expression {

    public static String translation(String original, Map<String,String> source){
        if(!original.contains("${")||!original.contains("}"))
            return original;
        List<String> $_key= Regular.getArrayByExpression(original.trim(),Regular.$_$);
        List<String> key=$_key.stream().map(a->a.substring(2,a.length()-1)).collect(Collectors.toList());
        for(int i=0;i<$_key.size();i++){
            original=original.replace($_key.get(i),source.get(key.get(i)));
        }
        return original;
    }

    public static String translationSharp(String original, Map<String,Object> source){
        if(!original.contains("#{")||!original.contains("}"))
            return original;
        List<String> $_key= Regular.getArrayByExpression(original.trim(),Regular.Sharp);
        List<String> key=$_key.stream().map(a->a.substring(2,a.length()-1)).collect(Collectors.toList());
        for(int i=0;i<$_key.size();i++){
            original=original.replace($_key.get(i),source.get(key.get(i)).toString());
        }
        return original;
    }
}
