package com.lucky.web.filter;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感字符过滤器
 * @author fk7075
 * @version 1.0
 * @date 2020/8/24 11:21
 */
public abstract class SensitiveCharactersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        ServletRequest proxy_req = (ServletRequest) Proxy.newProxyInstance(req.getClass().getClassLoader(), req.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //判断是否是getParameter方法
                //如果是，增强getParameter方法
                if ("getParameter".equals(method.getName())) {
                    //增强返回值
                    //获取返回值
                    String value = (String) method.invoke(req, args);
                    if (value != null) {
                        String[] arr={value};
                        value=sensitiveCharacterHandling(arr)[0];
                    }
                    return value;
                }else if("getParameterMap".equals(method.getName())){
                    Map<String,String[]> invoke = (Map<String, String[]>) method.invoke(req, args);
                    Map<String,String[]> invokeCopy=new HashMap<>();
                    for(Map.Entry<String,String[]> en:invoke.entrySet()){
                        invokeCopy.put(en.getKey(),sensitiveCharacterHandling(en.getValue()));
                    }
                    return invokeCopy;
                }
                return method.invoke(req, args);
            }
        });
        chain.doFilter(proxy_req, resp);
    }

    /**
     * 铭感词汇处理
     * @param input 原始参数[request.getParameterMap()]
     * @return 返回处理后的参数
     */
    protected abstract String[] sensitiveCharacterHandling(String[] input);
}
