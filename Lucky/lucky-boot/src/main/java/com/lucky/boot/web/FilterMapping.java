package com.lucky.boot.web;

import com.lucky.framework.container.Injection;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

public class FilterMapping extends WebMapping{
	
	private Filter filter;
	private String[] servletNames={};
	private DispatcherType[] dispatcherTypes={DispatcherType.REQUEST};

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
		Injection.injection(this.filter,"web-filter");
	}

	public String[] getServletNames() {
		return servletNames;
	}

	public void setServletNames(String[] servletNames) {
		this.servletNames = servletNames;
	}

	public DispatcherType[] getDispatcherTypes() {
		return dispatcherTypes;
	}

	public void setDispatcherTypes(DispatcherType[] dispatcherTypes) {
		this.dispatcherTypes = dispatcherTypes;
	}
}
