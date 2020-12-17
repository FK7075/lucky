package com.lucky.jacklamb.reverse;

import com.lucky.jacklamb.datasource.LuckyDataSource;
import com.lucky.jacklamb.datasource.LuckyDataSourceManage;
import com.lucky.jacklamb.jdbc.core.SqlOperation;
import com.lucky.utils.base.BaseUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 得到数据库中所有的表的名字
 * @author fk-7075
 *
 */
public class Tables {
	private List<String> tablenames=new ArrayList<String>();//数据库中表的名字

	public List<String> getTablenames() {
		return tablenames;
	}

	public void setTablenames(List<String> tablenames) {
		this.tablenames = tablenames;
	}
	/**
	 * 得到数据库中所有表的名字
	 */
	public Tables(String dbname) {
		SqlOperation sqlOperation
				=new SqlOperation(LuckyDataSourceManage.getDataSource(dbname).getConnection()
								 ,dbname,false);
		ResultSet rs= sqlOperation.getResultSet(dbname,"show tables;");
		try {
				while(rs.next()) {
					this.tablenames.add(BaseUtils.capitalizeTheFirstLetter(rs.getString(1)));
			}
		} catch (SQLException e) {
			throw new RuntimeException("无法获取表名！",e);
		}finally {
			LuckyDataSource.close(rs,null,null);
		}
	}
}
