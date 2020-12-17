package com.lucky.jacklamb.reverse;

public class TableNum {
	private Long tables;
	private String table_schema;

	public Long getTables() {
		return tables;
	}
	public void setTables(Long tables) {
		this.tables = tables;
	}
	public String getTable_schema() {
		return table_schema;
	}
	public void setTable_schema(String table_schema) {
		this.table_schema = table_schema;
	}
	@Override
	public String toString() {
		return "TableNum [tables=" + tables + ", table_schema=" + table_schema + "]";
	}

}
