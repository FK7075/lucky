package com.lucky.jacklamb.enums;

public enum Sort {
	
	ASC("ASC"),DESC("DESC");
	
	private String sort;
	
	private Sort(String sort) {
		this.sort=sort;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
}
