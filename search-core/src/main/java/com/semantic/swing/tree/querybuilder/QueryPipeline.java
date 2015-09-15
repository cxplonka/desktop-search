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
 * @author cplonka
 */
public class QueryPipeline {

    private List<IQueryBuilder> builders;
    private Query currentQuery;

    public Query getCurrentQuery() {
        return currentQuery;
    }    
    
    public Query generateQuery() {
        BooleanQuery ret = new BooleanQuery();
        for (IQueryBuilder builder : builders) {
            Query query = builder.createQuery();
            if (query != null) {
                ret.add(query, builder.getCondition());
            }
        }
        return currentQuery = ret;
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