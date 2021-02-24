package com.lucky.solr.core;


import com.lucky.solr.conf.SolrConfig;
import com.lucky.utils.annotation.NonNull;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2021/2/24 0024 15:43
 */
public class SolrUtils {

    private final static SolrConfig conf=SolrConfig.getSolrConfig();

    public static SolrClient getSolrClient(){
        assert conf != null: "SolrUrl is null";
        return new HttpSolrClient.Builder(conf.getSolrUrl())
                .withConnectionTimeout(conf.getWithConnectionTimeout())
                .withSocketTimeout(conf.getWithSocketTimeout())
                .build();
    }

    public static void addDocument(SolrInputDocument document) throws IOException, SolrServerException {
        SolrClient solrClient = getSolrClient();
        solrClient.add(document);
        solrClient.commit();
        solrClient.close();
    }

    public static void deleteDocumentByIds(String...documentId) throws IOException, SolrServerException {
        deleteDocumentByIds(Arrays.asList(documentId));
    }

    public static void deleteDocumentByIds(List<String> documentIds) throws IOException, SolrServerException {
        SolrClient solrClient = getSolrClient();
        solrClient.deleteById(documentIds);
        solrClient.commit();
        solrClient.close();
    }

    public static <T> List<T> query(@NonNull SolrQuery solrQuery, Class<T> tClass) throws IOException, SolrServerException {
        SolrClient solrClient = getSolrClient();
        QueryResponse response = solrClient.query(solrQuery);
        return response.getBeans(tClass);
    }

}
