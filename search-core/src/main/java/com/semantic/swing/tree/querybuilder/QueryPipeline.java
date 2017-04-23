/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.querybuilder;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class QueryPipeline {

    private List<IQueryBuilder> builders;
    private Query currentQuery;

    public Query getCurrentQuery() {
        return currentQuery;
    }

    public Query generateQuery() {
        return currentQuery = generateQuery(false);
    }

    public Query generateBaseQuery() {
        return generateQuery(true);
    }

    private Query generateQuery(boolean baseQuery) {
        BooleanQuery.Builder ret = new BooleanQuery.Builder();
        for (IQueryBuilder builder : builders) {
            Query query = null;
            if (baseQuery) {
                // only create basequery
                if (builder.isBaseQuery()) {
                    query = builder.createQuery();
                }
            } else {
                query = builder.createQuery();
            }

            if (query != null) {
                ret.add(query, builder.getCondition());
            }
        }
        return ret.build();
    }

    public void addQueryBuilder(IQueryBuilder builder) {
        if (builders == null) {
            builders = new ArrayList<IQueryBuilder>();
        }
        builders.add(builder);
    }

    public void removeQueryBuilder(IQueryBuilder builder) {
        if (builders != null) {
            builders.remove(builder);
        }
    }
}
