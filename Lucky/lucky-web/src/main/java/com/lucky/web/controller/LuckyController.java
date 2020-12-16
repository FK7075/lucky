package com.lucky.web.controller;

import com.lucky.web.core.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class LuckyController{

	private static final Logger log= LoggerFactory.getLogger(LuckyController.class);

	/** 当前请求的Model对象*/
	protected Model model;
	/** Request对象*/
	protected HttpServletRequest request;
	/** Response对象*/
	protected HttpServletResponse response;
	/** Session对象*/
	protected HttpSession session;
	/** ServletContext对象*/
	protected ServletContext application;
}
