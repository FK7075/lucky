package com.lucky.jacklamb.reverse;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.datasource.sql.NoDataSourceException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 负责将数据库中的表转化为对应的JavaBean
 * 
 * @author fk-7075
 *
 */
public class TableToJava {

	private LuckyDataSource data;
	private String dbname;

	public TableToJava(String dbname) {
		this.data = LuckyDataSourceManage.getDataSource(dbname);
		this.dbname = dbname;
	}

	/**
	 * 创建JavaBean(配置方式url)
	 */
	public void generateJavaSrc() {
		List<GetJavaSrc> src = JavaFieldGetSet.getMoreJavaSrc(dbname);
		String srcpath = data.getSrcPath();
		String packpath = data.getReversePack().replaceAll("\\.", "/");
		String url = srcpath + "/" + packpath;
		writerToJava(src, url, false, true);
	}

	/**
	 * 创建JavaBean(配置方式url,手动输入表名)
	 * 
	 * @param tables
	 *            表名
	 */
	public void b_generateJavaSrc(String... tables) {
		List<GetJavaSrc> src = JavaFieldGetSet.getAssignJavaSrc(dbname, tables);
		String srcpath = data.getSrcPath();
		String packpath = data.getReversePack().replaceAll("\\.", "/");
		String url = srcpath + "/" + packpath;
		writerToJava(src, url, false, false);
	}

	/**
	 * 创建JavaBean(手动输入url,手动输入表名)
	 * 
	 * @param srcPath
	 *            src文件绝对路径
	 * @param tables
	 *            需要逆向工程生成javabean的表
	 */
	public void a_generateJavaSrc(String srcPath, String... tables) {
		List<GetJavaSrc> src = JavaFieldGetSet.getAssignJavaSrc(dbname, tables);
		String packpath = data.getReversePack().replaceAll("\\.", "/");
		String url = srcPath + "/" + packpath;
		writerToJava(src, url, true, false);
	}

	/**
	 * 创建JavaBean(手动输入url)
	 * 
	 * @param srcPath
	 *            src文件夹的绝对路径
	 */
	public void generateJavaSrc(String srcPath) {
		List<GetJavaSrc> src = JavaFieldGetSet.getMoreJavaSrc(dbname);
		String packpath = data.getReversePack().replaceAll("\\.", "/");
		String url = srcPath + "/" + packpath;
		writerToJava(src, url, true, true);
	}

	/**
	 * 生成java源文件
	 * 
	 * @param src
	 *            源代码类
	 * @param path
	 *            位置
	 * @param isManual
	 *            是否手动
	 * @param ispackBox
	 *            是否生成PackBox
	 */
	private void writerToJava(List<GetJavaSrc> src, String path, boolean isManual, boolean ispackBox) {
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
		BufferedWriter bw = null;
		for (GetJavaSrc getJavaSrc : src) {
			try {
				bw = new BufferedWriter(new FileWriter(new File(path + "/" + getJavaSrc.getClassName() + ".java")));
				System.out.println(path + "/" + getJavaSrc.getClassName() + ".java");
				bw.write(getJavaSrc.getPack());
				bw.write("\n");
				bw.write("\n");
				bw.write(getJavaSrc.getImpor());
				bw.write("\n");
				bw.write("\n");
				bw.write(getJavaSrc.getJavaSrc());
				bw.write(getJavaSrc.getToString());
				bw.close();
			} catch (IOException e) {
				if (isManual)
					throw new NoDataSourceException(
							"不正确的逆向工程配置信息，无法执行JavaBean生成程序，请检查classpath下的db.properties配置文件中的'reverse.package'属性的配置信息，或者检查appconfig包下配置类[ApplicationConfig子类]的setDataSource方法是否正确。");
				else
					throw new NoDataSourceException(
							"不正确的逆向工程配置信息，无法执行JavaBean生成程序，请检查classpath下的db.properties配置文件中的'reverse.package'和srcpath属性的配置信息，或者检查appconfig包下配置类[ApplicationConfig子类]的setDataSource方法是否正确。");
			}
		}
		if (ispackBox) {
			PackBoxSrc p = PackBoxSrc.getPackBoxSrc(dbname);
			try {
				bw = new BufferedWriter(new FileWriter(new File(path + "/" + p.getClassName() + ".java")));
				System.out.println(path + "/" + p.getClassName() + ".java");
				bw.write(p.getPack());
				bw.write(p.getImpor());
				bw.write(p.getField());
				bw.write(p.getGetset());
				bw.write(p.getEnd());
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
