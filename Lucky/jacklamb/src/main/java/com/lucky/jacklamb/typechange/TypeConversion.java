package com.lucky.jacklamb.typechange;

import com.lucky.jacklamb.exception.NotSupportDataBasesException;

public abstract class TypeConversion {

	/**
	 * 将数据库类型转化为java类型的字符串表示(用于逆向工程)
	 * @param datype 数据库类型
	 * @return
	 */
	public String dbTypeToJava(String datype) {
		if("varchar".equalsIgnoreCase(datype)||"char".equalsIgnoreCase(datype)||"text".equalsIgnoreCase(datype)
		 ||"enum".equalsIgnoreCase(datype)||"set".equalsIgnoreCase(datype)||"graphic ".equalsIgnoreCase(datype)
		 ||"clob".equalsIgnoreCase(datype)||"dbclob".equalsIgnoreCase(datype)||"long varchar".equalsIgnoreCase(datype)
		 ||"long vargraphic".equalsIgnoreCase(datype)||"vargraphic".equalsIgnoreCase(datype)||"tinytext".equalsIgnoreCase(datype))
			return "String";
		if("tinyint".equalsIgnoreCase(datype)||"smallint".equalsIgnoreCase(datype)||"mediumint".equalsIgnoreCase(datype)
		 ||"int".equalsIgnoreCase(datype)||"integer".equalsIgnoreCase(datype)||"number".equalsIgnoreCase(datype))
			return "Integer";
		if("double".equalsIgnoreCase(datype)||"precision".equalsIgnoreCase(datype)||"float".equalsIgnoreCase(datype))
			return "Double";
		if("bigint".equalsIgnoreCase(datype))
			return "Long";
		if("blob".equalsIgnoreCase(datype)||"image".equalsIgnoreCase(datype)||"long raw".equalsIgnoreCase(datype)
		 ||"binary".equalsIgnoreCase(datype)||"varbinary".equalsIgnoreCase(datype)||"raw".equalsIgnoreCase(datype))
			return "byte[]";
		if("bit".equalsIgnoreCase(datype)||"n/a".equalsIgnoreCase(datype))
			return "Boolean";
		if("decimal".equalsIgnoreCase(datype)||"numeric".equalsIgnoreCase(datype)||"money".equalsIgnoreCase(datype)
		 ||"smallmoney".equalsIgnoreCase(datype))
			return "BigDecimal";
		if("date".equalsIgnoreCase(datype)||"year".equalsIgnoreCase(datype))
			return "Date";
		if("time".equalsIgnoreCase(datype))
			return "Time";
		if("datetime".equalsIgnoreCase(datype)||"smalldatetime".equalsIgnoreCase(datype)||"timestamp".equalsIgnoreCase(datype))
			return "Timestamp";
		throw new NotSupportDataBasesException("Lucky目前还不支持该数据库的该类型字段的转化"+datype);
	}
	
	/**
	 * 将java类型转化为数据库类型的字符串表示(用于自动建表)
	 * @param javaType javaClass
	 * @return
	 */
	public abstract String javaTypeToDb(String javaType);


}
