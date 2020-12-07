package com.lucky.boot.web;

import javax.servlet.http.HttpServlet;

public class ServletMapping extends WebMapping{

	private HttpServlet servlet;
	private int loadOnStartup=-1;

	public int getLoadOnStartup() {
		return loadOnStartup;
	}

	public void setLoadOnStartup(int loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public HttpServlet getServlet() {
		return servlet;
	}

	public void setServlet(HttpServlet servlet) {
		this.servlet = servlet;
	}
}
