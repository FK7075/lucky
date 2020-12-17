package com.lucky.jacklamb.jdbc.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 规则定义器，定义生成动态SQL的规则
 * @author fk7075
 * @version 1.0
 * @date 2020/8/19 10:24
 */
public class SplicingRules {

    private StringBuilder pSql;

    private List<Object> params;

    private String addPrefix;
    private String addSuffix;
    private String removePrefix;
    private String removeSuffix;

    /**
     * 添加固定前缀
     * @param prefix
     */
    public void addFixedPrefix(String prefix) {
        this.addPrefix = prefix;
    }

    /**
     * 添加固定后缀
     * @param suffix
     */
    public void addFixedSuffix(String suffix) {
        this.addSuffix = suffix;
    }

    /**
     * 去除SQL拼接过程产生的多余前缀
     * @param redundantPrefix
     */
    public void removeRedundantPrefix(String redundantPrefix) {
        this.removePrefix = redundantPrefix;
    }

    /**
     * 去除SQL拼接过程产生的多余后缀
     * @param redundantSuffix
     */
    public void removeRedundantSuffix(String redundantSuffix) {
        this.removeSuffix = redundantSuffix;
    }

    public SplicingRules() {
        pSql=new StringBuilder("");
        params=new ArrayList<>();
    }

    public SplicingRules(String pSql, List<Object> params) {
        this.pSql =new StringBuilder(pSql);
        this.params = params;
    }

    public SplicingRules addSqlAndParam(String sqlPassage, Object...params){
        pSql.append(sqlPassage);
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    /**
     * 获取当前PSQL
     * @return
     */
    public String getCurrPSql(){
        return pSql.toString();
    }


    public String getpSql(){
        //去前缀
        if(removePrefix!=null){
            String currsql = pSql.toString();
            if(currsql.trim().startsWith(removePrefix.trim())){
                int sp=currsql.indexOf(removePrefix.trim());
                pSql=new StringBuilder(pSql.substring(sp+removePrefix.trim().length()));
            }
        }
        if(addPrefix!=null){
            String currsql = pSql.toString();
            if(!"".equals(currsql)&&!currsql.trim().startsWith(addPrefix.trim())){
                pSql=new StringBuilder(String.format(" %s ",addPrefix.trim())).append(pSql);
            }
        }
        // 去后缀
        if(removeSuffix!=null){
            String currsql = pSql.toString();
            if(currsql.trim().endsWith(removeSuffix.trim())){
                int sp=currsql.lastIndexOf(removeSuffix.trim());
                pSql=new StringBuilder(pSql.substring(0,sp));
            }
        }
        if(addSuffix!=null){
            if(!pSql.toString().trim().endsWith(addSuffix.trim()))
                pSql.append(String.format(" %s ",addSuffix.trim()));
        }
        return pSql.toString();
    }

    public void setpSql(String sqlPassage){
        pSql.append(sqlPassage);
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public void addParams(Object...params) {
        Stream.of(params).forEach(this.params::add);
    }

}


