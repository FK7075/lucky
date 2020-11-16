package com.lucky.framework.uitls.conversion;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 表达式解析引擎
 * @author fk-7075
 *
 */
public abstract class ExpressionEngine {
	
	/**
	 * 运算符优先级
	 * @param ch
	 * @return
	 */
	private static int priority(String ch) {
		switch (ch) {
		case "(":
			return -1;
		case ")":
			return -1;
		case "^":
			return -2;
		case "*":
			return -3;
		case "/":
			return -3;
		case "%":
			return -3;
		case "+":
			return -4;
		case "-":
			return -4;
		case "<":
			return -5;
		case "<=":
			return -5;
		case ">":
			return -5;
		case ">=":
			return -5;
		case "==":
			return -6;
		case "!=":
			return -6;
		case "&&":
			return -7;
		case "||":
			return -8;
		default:
			return 0;
		}
	}

	
	public static Object parsing(String expression) {
		priority(expression);
		return true;
	}
	
	//"#[5]str#[4]"->"object[4]+str+object[3]"
	public static String removeSymbol(String original,Object[] object,String startStr,String endStr) {
		if(!original.contains(startStr)&&!original.contains(endStr))
			return original;
		int start=original.indexOf(startStr);
		int end=original.indexOf(endStr)+endStr.length();
		String firstStr=original.substring(start,end);
		firstStr=firstStr.substring(startStr.length(), firstStr.length()-endStr.length());
		int index=Integer.parseInt(firstStr.trim());
		if(index<1||index>object.length) {
			throw new RuntimeException("错误的表达式，表达式中的索引超出参数列表索引范围！错误位置："+original.substring(start,end));
		}
		original=original.substring(0,start)+object[index-1]+original.substring(end+endStr.length()-1);
		return removeSymbol(original,object,startStr,endStr);
	}
	
	public static String calculate(String expression) {
		if("".equals(expression))
			return "";
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
        try {
            String result = String.valueOf(scriptEngine.eval(expression));
            return result;
        } catch (ScriptException e) {
        	return expression;
        }
    }

	public static void main(String[] args) {
		System.out.println(calculate("1024*10"));
	}
}
