package com.lucky.solr.conf;

import com.lucky.framework.confanalysis.LuckyConfig;

/**
 * Solr的配置类
 * @author fk
 * @version 1.0
 * @date 2021/2/24 0024 15:27
 */
public class SolrConfig extends LuckyConfig {

    private static SolrConfig conf;
    private String solrUrl;
    private int withConnectionTimeout;
    private int withSocketTimeout;
    private String highlightSimplePre;
    private String highlightSimplePost;

    private SolrConfig(){};

    public String getSolrUrl() {
        return solrUrl;
    }

    public void setSolrUrl(String solrUrl) {
        this.solrUrl = solrUrl;
    }

    public int getWithConnectionTimeout() {
        return withConnectionTimeout;
    }

    public void setWithConnectionTimeout(int withConnectionTimeout) {
        this.withConnectionTimeout = withConnectionTimeout;
    }

    public int getWithSocketTimeout() {
        return withSocketTimeout;
    }

    public void setWithSocketTimeout(int withSocketTimeout) {
        this.withSocketTimeout = withSocketTimeout;
    }

    public String getHighlightSimplePre() {
        return highlightSimplePre;
    }

    public void setHighlightSimplePre(String highlightSimplePre) {
        this.highlightSimplePre = highlightSimplePre;
    }

    public String getHighlightSimplePost() {
        return highlightSimplePost;
    }

    public void setHighlightSimplePost(String highlightSimplePost) {
        this.highlightSimplePost = highlightSimplePost;
    }

    public static SolrConfig defaultSolrConfig(){
        if(conf==null){
            conf=new SolrConfig();
        }
        return conf;
    }

    public static SolrConfig getSolrConfig(){
        return conf;
    }

    @Override
    public void loadYaml() {

    }
}
