package com.lucky.jacklamb.jdbc.core.abstcore;

/**
 * SQL组合
 * @author DELL
 *
 */
public abstract class SqlGroup {
	
	protected Integer page;
	
	protected Integer rows;
	
	
	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	/**
	 * 查询条件的组合方式
	 * @param res 返回列
	 * @param onsql Join的ON部分
	 * @param andsql Join的And部分
	 * @param like 模糊查询条件
	 * @param sort 排序查询条件
	 * @return
	 */
	public abstract String sqlGroup(String res,String onsql,String andsql,String like,String sort);

}
