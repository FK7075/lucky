package com.lucky.jacklamb.boot.startup;

import com.lucky.framework.ApplicationContext;
import com.lucky.framework.uitls.base.Assert;
import com.lucky.jacklamb.boot.conf.ServerConfig;
import com.lucky.jacklamb.boot.web.FilterMapping;
import com.lucky.jacklamb.boot.web.ServletMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.Set;

public class LuckyServletContainerInitializer implements ServletContainerInitializer {
	
	public final ServerConfig serverCfg=ServerConfig.getServerConfig();

	private static final Logger log= LogManager.getLogger("c.l.j.s.LuckyServletContainerInitializer");
	
	public LuckyServletContainerInitializer(ApplicationContext applicationContext) {
		serverCfg.init(applicationContext);
	}


	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
		ServletRegistration.Dynamic servlet;
		FilterRegistration.Dynamic filter;
		String[] mapping;
		for(ServletMapping sm:serverCfg.getServletList()) {
			servlet=ctx.addServlet(sm.getServletName(), sm.getServlet());
			servlet.setLoadOnStartup(sm.getLoadOnStartup());
			mapping=new String[sm.getRequestMapping().size()];
			mapping=sm.getRequestMapping().toArray(mapping);
			servlet.addMapping(mapping);
			log.info("Add Servlet `name="+sm.getServletName()+" mapping="+Arrays.toString(mapping)+" class="+sm.getServlet().getClass().getName()+"`");
		}
		
		for(FilterMapping fm:serverCfg.getFilterList()) {
			filter=ctx.addFilter(fm.getFilterName(), fm.getFilter());
			mapping=new String[fm.getRequestMapping().size()];
			mapping=fm.getRequestMapping().toArray(mapping);
			filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true,mapping);
			log.info("Add Filter `[name="+fm.getFilterName()+" mapping="+Arrays.toString(mapping)+" class="+ fm.getFilter().getClass().getName()+"]`");
		}
		
		for(EventListener l:serverCfg.getListeners()) {
			ctx.addListener(l);
			log.info("Add Listener `[class="+l.getClass().getName()+"]`");
		}
		log.info("Tomcat SessionTimeOut \"" +serverCfg.getSessionTimeout()+"min\"");
		if(!Assert.isNull(serverCfg.getClosePort())){
			log.info("Tomcat Shutdown-Port \"" +serverCfg.getClosePort()+"\"");
		}
		if(!Assert.isNull(serverCfg.getShutdown())){
			log.info("Tomcat Shutdown-Command \"" +serverCfg.getShutdown()+"\"");
		}
		log.info("Tomcat BaseDir \"" +serverCfg.getBaseDir()+"\"");
		if(!Assert.isNull(serverCfg.getDocBase())){
			log.info("Tomcat DocBase \"" +serverCfg.getDocBase()+"\"");
		}
		log.info("Tomcat ContextPath : \"" +serverCfg.getContextPath()+"\"");
	}

}
