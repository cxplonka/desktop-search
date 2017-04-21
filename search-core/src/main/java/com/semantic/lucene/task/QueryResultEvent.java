/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.task;

import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class QueryResultEvent {

    private final Query query;
    private final TopDocs topDocs;
    private final IndexSearcher currentSearcher;
    private final FacetsConfig facetConfig;

    public QueryResultEvent(IndexSearcher searcher, Query query, TopDocs topDocs, FacetsConfig facetConfig) {
        this.query = query;
        this.topDocs = topDocs;
        this.currentSearcher = searcher;
        this.facetConfig = facetConfig;
    }

    public IndexSearcher getCurrentSearcher() {
        return currentSearcher;
    }

    public Query getQuery() {
        return query;
    }

    public TopDocs getTopDocs() {
        return topDocs;
    }

    public FacetsConfig getFacetConfig() {
        return facetConfig;
    }
}
