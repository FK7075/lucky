package com.lucky.web.mapping;

import com.lucky.web.core.Model;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.exception.RepeatUrlMappingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 10:03
 */
public class MappingCollection {

    private List<Mapping> list;

    public MappingCollection(){
        list=new ArrayList<>(10);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        if(!(o instanceof Mapping)){
            return false;
        }
        Mapping map= (Mapping) o;
        for (Mapping mapping : list) {
            if(mapping.isEquals(map)){
                return true;
            }
        }
        return false;
    }

    public Iterator<Mapping> iterator() {
        return list.iterator();
    }

    public boolean add(Mapping mapping) {
        if(contains(mapping)){
            throw new RepeatUrlMappingException(mapping);
        }
        list.add(mapping);
        return true;
    }

    public void clear() {
        list.clear();
    }

    public Mapping getMapping(Model model){
        Mapping mapping=getByUrl(model);
        if(mapping==null){
            model.error("404", "找不与请求相匹配的映射资,请检查您的URL是否正确！","不正确的url："+model.getUri());
            return null;
        }
        String currIp=model.getIpAddr();
        if (!mapping.ipExistsInRange(currIp) || !mapping.ipISCorrect(currIp)) {
            model.error("403", "该ip地址没有被注册，服务器拒绝响应！", "不合法的请求ip：" + currIp);
            return null;
        }
        RequestMethod method=model.getRequestMethod();
        if(!mapping.methodIsEquals(method)){
            model.error("403","您的请求类型["+method+"] , 当前方法并不支持！","不合法的请求类型["+method+"]!");
            return null;
        }
        return mapping;
    }

    private Mapping getByUrl(Model model){
        String url=model.getUri();
        for (Mapping mapping : list) {
            if(mapping.urlIsEquals(url)){
                return mapping;
            }
            if(restUrlVerify(model,mapping.getUrl())){
                return mapping;
            }
        }
        return null;
    }

    private Mapping getByMethod(Model model){
        return null;
    }

    private boolean restUrlVerify(Model model,String confUrl){
        return false;
    }
}
