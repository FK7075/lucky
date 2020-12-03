package com.lucky.jacklamb.boot.web;

import javax.servlet.Filter;
import java.util.Set;

public class FilterMapping {
	
	private Set<String> requestMapping;
	
	private String filterName;
	
	private Filter filter;
	
	
	public FilterMapping(Set<String> requestMapping, String filterName, Filter filter) {
		this.requestMapping = requestMapping;
		this.filterName = filterName;
		this.filter = filter;
	}
	
	public Set<String> getRequestMapping() {
		return requestMapping;
	}

	public void setRequestMapping(Set<String> requestMapping) {
		this.requestMapping = requestMapping;
	}

	public String getFilterName() {
		return filterName;
	}
	
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	
	public Filter getFilter() {
		return filter;
	}
	
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	

}
