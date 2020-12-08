package com.lucky.web.controller;

import com.lucky.web.annotation.GetMapping;
import com.lucky.web.annotation.InitRun;
import com.lucky.web.annotation.ResponseBody;
import com.lucky.web.enums.Rest;
import com.lucky.web.mapping.UrlMapping;
import com.lucky.web.mapping.UrlMappingCollection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 *
 * @author fk
 * @version 1.0
 * @date 2020/12/8 0008 16:13
 */
public abstract class JarExpandDetailsController extends JarExpandController{

    /**
     * 获取所有的扩展信息
     * @return
     */
    @ResponseBody
    @GetMapping("/allJarExpand")
    public Collection<JarExpand> getAllJarExpand(){
        return getUrlMappingCollection().getExpandInfoMap().values();
    }

    /**
     * 获取所有过期的扩展信息【被逻辑删除的扩展】
     * @return
     */
    @ResponseBody
    @GetMapping("/invalidJarExpand")
    public Collection<JarExpand> getLogicDelJarExpand(){
        Set<String> deleteExpand = getUrlMappingCollection().getDeleteExpand();
        Map<String, JarExpand> expandInfoMap = getUrlMappingCollection().getExpandInfoMap();
        List<JarExpand> invalid=new ArrayList<>(deleteExpand.size());
        for (String expandName : deleteExpand) {
            invalid.add(expandInfoMap.get(expandName));
        }
        return invalid;
    }
}
