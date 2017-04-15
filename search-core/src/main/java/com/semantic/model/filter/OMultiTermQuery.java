/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public abstract class OMultiTermQuery extends OntologyNode implements IQueryGenerator {

    /* cache query */
    protected Query query;

    @Override
    public Query createQuery() {
        if (query == null) {
            BooleanQuery.Builder root = new BooleanQuery.Builder();
            for (String term : getTerms()) {
                root.add(new TermQuery(new Term(getLuceneField(), term)), BooleanClause.Occur.SHOULD);
            }
            query = root.build();
        }
        return query;
    }

    public abstract String[] getTerms();
}
