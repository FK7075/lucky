package com.lucky.web.interceptor;

import com.lucky.utils.base.Assert;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/5 0005 14:18
 */
public class PathAndInterceptor {

    private String[] path;
    private String[] excludePath;
    private double priority=5;
    private HandlerInterceptor interceptor;

    public PathAndInterceptor() {
    }

    public PathAndInterceptor(String[] paths, HandlerInterceptor interceptor) {
        this.path = paths;
        this.interceptor = interceptor;
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

    public HandlerInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(HandlerInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public boolean pathCheck(String currPath){
        //既没有配置path，也没有配置excludePath,则此拦截器始终不会生效
        if(Assert.isEmptyArray(path) && Assert.isEmptyArray(excludePath)){
            return false;
        }
        //判断当前URL是否存在与该拦截器的拦截列表中
        boolean pathCheck=pathCheck(path,currPath);

        //判断当前URL是否不存在与该拦截器的排除列表中
        boolean excludePathCheck= Assert.isEmptyArray(excludePath) || !pathCheck(excludePath, currPath);

        //当前URL存在与该拦截器的拦截列表中，且不存在与排除列表中，此时此拦截器生效
        return pathCheck && excludePathCheck;
    }

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

    private boolean pathCheck(String confPath,String currPath){
        if("/**".equals(confPath)){
            return true;
        }
        String name;
        if(confPath.endsWith("/*")){
            name=confPath.substring(0,confPath.length()-2);
            return currPath.startsWith(name);
        }
        if(confPath.startsWith("*/")){
            name=confPath.substring(2);
            return currPath.endsWith(name);
        }
        return currPath.equals(confPath);
    }


}
