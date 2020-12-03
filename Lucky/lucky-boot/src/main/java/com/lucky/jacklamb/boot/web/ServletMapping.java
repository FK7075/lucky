package com.lucky.jacklamb.boot.web;

import javax.servlet.http.HttpServlet;
import java.util.Set;

public class ServletMapping {
	
	private Set<String> requestMapping;
	
	private String servletName;

	private int loadOnStartup;

	private HttpServlet servlet;
	
	public ServletMapping(Set<String> requestMapping, String servletName, HttpServlet servlet,int loadOnStartup) {
		init(requestMapping,servletName,servlet,loadOnStartup);
	}

	public ServletMapping(Set<String> requestMapping, String servletName, HttpServlet servlet){
		init(requestMapping,servletName,servlet,-1);
	}

	private void init(Set<String> requestMapping, String servletName, HttpServlet servlet,int loadOnStartup){
		this.requestMapping = requestMapping;
		this.loadOnStartup=loadOnStartup;
		this.servletName = servletName;
		this.servlet = servlet;
	}

	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	public void setLoadOnStartup(int loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public Set<String> getRequestMapping() {
		return requestMapping;
	}

	public void setRequestMapping(Set<String> requestMapping) {
		this.requestMapping = requestMapping;
	}

	public String getServletName() {
		return servletName;
	}
	
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	
	public HttpServlet getServlet() {
		return servlet;
	}
	
	public void setServlet(HttpServlet servlet) {
		this.servlet = servlet;
	}
	

}
