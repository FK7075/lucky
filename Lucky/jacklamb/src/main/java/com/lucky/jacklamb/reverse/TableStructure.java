package com.lucky.jacklamb.reverse;

import com.lucky.jacklamb.datasource.LuckyDataSource;
import com.lucky.jacklamb.datasource.LuckyDataSourceManage;
import com.lucky.jacklamb.jdbc.core.SqlOperation;
import com.lucky.jacklamb.typechange.JDBChangeFactory;
import com.lucky.jacklamb.typechange.TypeConversion;
import com.lucky.utils.base.BaseUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 表到类的信息封装器（类名 属性名 属性类型 toString方法）
 * @author fk-7075
 *
 */
public class TableStructure {
	private String tableName;//表名
	private List<String> fields=new ArrayList<String>();//字段名集
	private List<String> types=new ArrayList<String>();//对应的类型集
	private String pri;//主键
	private List<String> muls=new ArrayList<String>();//外键
	private static String dbname;
	
	
	@Override
	public String toString() {
		return "TableStructure [tableName=" + tableName + ", fields=" + fields + ", types=" + types + ", pri=" + pri
				+ ", muls=" + muls + "]";
	}
	
	

	public String getTableName() {
		return tableName;
	}



	public void setTableName(String tableName) {
		this.tableName = tableName;
	}



	public List<String> getFields() {
		return fields;
	}



	public void setFields(List<String> fields) {
		this.fields = fields;
	}



	public List<String> getTypes() {
		return types;
	}



	public void setTypes(List<String> types) {
		this.types = types;
	}



	public String getPri() {
		return pri;
	}



	public void setPri(String pri) {
		this.pri = pri;
	}



	public List<String> getMuls() {
		return muls;
	}



	public void setMuls(String mul) {
		this.muls.add(mul);
	}



	/**
	 * 获得tableName表对应的表的结构
	 * @param tableName 表名
	 */
	public TableStructure(String dbname,String tableName) {
		TypeConversion jDChangeFactory = JDBChangeFactory.jDBChangeFactory(dbname);
		TableStructure.dbname=dbname;
		this.tableName= BaseUtils.capitalizeTheFirstLetter(tableName);
		SqlOperation sqlOperation
				=new SqlOperation(LuckyDataSourceManage.getDataSource(dbname).getConnection()
				,dbname,false);
		ResultSet rs=sqlOperation.getResultSet(dbname,"DESCRIBE "+tableName);
		try {
			while(rs.next()) {
				this.getFields().add(rs.getString("Field"));
				if("PRI".equalsIgnoreCase(rs.getString("Key")))
					this.setPri(rs.getString("Field"));
				if("MUL".equalsIgnoreCase(rs.getString("Key")))
					this.setMuls(rs.getString("Field"));
				this.getTypes().add(jDChangeFactory.dbTypeToJava(getMySqlType(rs.getString("Type"))));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}finally {
			LuckyDataSource.close(rs,null,null);
		}
	}
	
	/**
	 * 返回指定表的结构
	 * @param tables 表名
	 * @return
	 */
	public static List<TableStructure> getAssignTableStructure(String dbname,String...tables){
		List<TableStructure> list=new ArrayList<TableStructure>();
		for (String tablename : tables) {
			TableStructure table=new TableStructure(dbname,tablename);
			list.add(table);
		}
		return list;
	}
	/**
	 * 获得数据库中所有表的结构
	 * @param tables Tables对象
	 * @return 
	 */
	public static List<TableStructure> getMoreTableStructure(String dbname,Tables tables){
		List<TableStructure> list=new ArrayList<TableStructure>();
		for (String t_name : tables.getTablenames()) {
			TableStructure table=new TableStructure(dbname,t_name);
			list.add(table);
		}
		return list;
	}
	/**
	 * 得到该表对应类的toString方法的源代码 
	 * @return
	 */
	public String getToString() {
		String tostr="\n\t@Override\n\tpublic String toString() {\n\t\treturn \""+tableName+" [";
		for(int i=0;i<fields.size();i++) {
			if(i!=fields.size()-1) {
				tostr+=fields.get(i)+"=\" + "+fields.get(i)+" + \", ";
			}else{
				tostr+=fields.get(i)+"=\" + "+fields.get(i)+" + \"]\";\n\t}\n}";
			}
		}
		return tostr;
	}
	/**
	 * 无参构造器的源代码
	 * @return
	 */
	public String getConstructor() {
		String ctsrc="\tpublic "+tableName+"(){}";
		return ctsrc;
	}
	/**
	 * 返回包含该表全部属性的有参构造器的源代码
	 * @return
	 */
	public String getParameterConstructor() {
		String pctsrc="\tpublic "+tableName+"(";
		for(int i=0;i<fields.size();i++) {
			if(i!=fields.size()-1)
				pctsrc+=types.get(i)+" "+fields.get(i)+", ";
			else
				pctsrc+=types.get(i)+" "+fields.get(i)+"){\n";
		}
		for(int i=0;i<fields.size();i++) {
			if(i!=fields.size()-1)
				pctsrc+="\t\tthis."+fields.get(i)+"="+fields.get(i)+";\n";
			else
				pctsrc+="\t\tthis."+fields.get(i)+"="+fields.get(i)+";\n\t}";
		}
		return pctsrc;
	}

	/**
	 * 获得属性的类型去掉长度
	 * @param type 带长度的属性类型
	 * @return 不带长度的属性
	 */
	public static String getMySqlType(String type) {
		if(type.indexOf("(")>=0)
			return type.substring(0, type.indexOf("("));
		else
			return type;
	}
}
