package com.lucky.web.interceptor;

import com.lucky.framework.container.Module;
import com.lucky.framework.container.factory.Namer;
import com.lucky.utils.base.Assert;
import com.lucky.utils.io.utils.AntPathMatcher;

/**
 * 拦截器和该拦截器的作用范围
 * @author fk
 * @version 1.0
 * @date 2021/1/5 0005 14:18
 */
public class PathAndInterceptor {

    private static final AntPathMatcher antPathMatcher=new AntPathMatcher();
    /*
        path和excludePath同时配置时，两者同时生效，真实的作用为两者取交集
        也即，只有当某个资源存在于path列表中，但又不存在于excludePath列表
        中时，此拦截器才会生效！

        当path和excludePath均没有配置时，此拦截器将被视为无效拦截器，永不生效！
     */

    /** 需要拦截的路径*/
    private String[] path;
    /** 需要排除的路径*/
    private String[] excludePath;
    /** 执行的优先级*/
    private double priority=5;
    /** 拦截器实例*/
    private Module interceptor;

    public PathAndInterceptor() {
    }

    public PathAndInterceptor(String[] paths, HandlerInterceptor interceptor) {
        this.path = paths;
        this.interceptor = new Module(Namer.getBeanName(interceptor.getClass()),"interceptor",interceptor);
    }

    public PathAndInterceptor(HandlerInterceptor interceptor,String...path){
        this(path,interceptor);
    }

    public String[] getExcludePath() {
        return excludePath;
    }

    public void setExcludePath(String...excludePath) {
        this.excludePath = excludePath;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String...path) {
        this.path = path;
    }

    public Module getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(HandlerInterceptor interceptor) {
        this.interceptor = new Module(Namer.getBeanName(interceptor.getClass()),"interceptor",interceptor);
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    /**
     * 路径校验，判断给定的URL是否在本拦截器的作用范围内
     * @param currPath 给定的URL
     * @return 在作用范围返回true，否则返回false
     */
    public boolean pathCheck(String currPath){
        //既没有配置path，也没有配置excludePath,则此拦截器始终不会生效
        if(Assert.isEmptyArray(path) && Assert.isEmptyArray(excludePath)){
            return false;
        }
        //判断当前URL是否存在于该拦截器的拦截列表中
        boolean pathCheck=pathCheck(path,currPath);

        //判断当前URL是否不存在于该拦截器的排除列表中
        boolean excludePathCheck= Assert.isEmptyArray(excludePath) || !pathCheck(excludePath, currPath);

        //当前URL存在于该拦截器的拦截列表中，且不存在于排除列表中，此时此拦截器生效
        return pathCheck && excludePathCheck;
    }

    //判断给定的URL是否匹配一组拦截规则
    private boolean pathCheck(String[] paths,String currPath) {
        if(Assert.isEmptyArray(paths)){
            return true;
        }
        for (String confPath : paths) {
            if(pathCheck(confPath,currPath)){
                return true;
            }
        }
        return false;
    }

    //判断给定URL是否匹配某个拦截规则
    private boolean pathCheck(String confPath,String currPath){
        return antPathMatcher.isPattern(confPath)
                ?antPathMatcher.match(confPath,currPath): confPath.equals(currPath);
        // </**> 表示所有资源
//        if("/**".equals(confPath)){
//            return true;
//        }
//        String name;
//        // </*> 表示以某个字符开头
//        if(confPath.endsWith("/*")){
//            name=confPath.substring(0,confPath.length()-2);
//            return currPath.startsWith(name);
//        }
//        // <*/> 表示以某个字符结尾
//        if(confPath.startsWith("*/")){
//            name=confPath.substring(2);
//            return currPath.endsWith(name);
//        }
//        // 全匹配
//        return currPath.equals(confPath);
    }
}
