package com.lucky.jacklamb.reverse;

import com.lucky.jacklamb.datasource.LuckyDataSourceManage;
import com.lucky.utils.base.BaseUtils;

import java.util.List;

/**
 * 万能包装箱
 * 
 * @author fk-7075
 *
 */
public class PackBoxSrc {

	private String className;// 类名
	private String pack;// 所在包
	private String impor;// 导入的包
	private String field;
	private String getset;
	private String end;

	public String getImpor() {
		return impor;
	}

	public void setImpor(String impor) {
		this.impor = impor;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPack() {
		return pack;
	}

	public void setPack(String pack) {
		this.pack = pack;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getGetset() {
		return getset;
	}

	public void setGetset(String getset) {
		this.getset = getset;
	}

	
	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public static PackBoxSrc getPackBoxSrc(String dbname,String... classnames) {
		PackBoxSrc pack = new PackBoxSrc();
		pack.setClassName("PackBox");
		pack.setPack("package " + LuckyDataSourceManage.getDataSource(dbname).getCreateTable() + ";\n\n/**\n * 万能打包器，任何数据库的操作都可以由此类来包装\n * @author FK7075\n */");
		pack.setImpor("\npublic class PackBox {\n\n");
		pack.setField("");
		pack.setGetset("");
		pack.setEnd("\n}");
		if (classnames.length == 0) {
			Tables table=new Tables(dbname);
			List<String> list=table.getTablenames();
			String[] tabs=new String[list.size()];
			tabs=list.toArray(tabs);
			for (int i=0;i<tabs.length;i++) {
				tabs[i]= BaseUtils.lowercaseFirstLetter(tabs[i]);
			}
			classnames=tabs;
		}
		for (String name : classnames) {
			StringBuilder sb1=new StringBuilder();
			String Name=BaseUtils.capitalizeTheFirstLetter(name);
			sb1.append("\t").append("private ").append(Name+" ").append(name+";\n");
			StringBuilder sb2=new StringBuilder();
			sb2.append("\n\n\tpublic ").
			append(Name+" get"+Name+"() {\n\t\treturn this."+name+";\n\t}\n\n\tpublic void set"+Name+"("+Name+" "+name+") {\n"
					+ "\t\tthis."+name+" = "+name+";\n\t}");
			pack.setField(pack.getField()+sb1.toString());
			pack.setGetset(pack.getGetset()+sb2.toString());
		}
		return pack;
	}

}
