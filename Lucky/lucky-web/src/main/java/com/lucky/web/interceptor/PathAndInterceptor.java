package com.lucky.web.interceptor;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/5 0005 14:18
 */
public class PathAndInterceptor {

    private String[] path;
    private HandlerInterceptor interceptor;

    public PathAndInterceptor() {
    }

    public PathAndInterceptor(String[] paths, HandlerInterceptor interceptor) {
        this.path = paths;
        this.interceptor = interceptor;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public HandlerInterceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(HandlerInterceptor interceptor) {
        this.interceptor = interceptor;
    }


    public boolean pathCheck(String path){
        for (String confPath : this.path) {
            if(pathCheck(confPath,path)){
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
