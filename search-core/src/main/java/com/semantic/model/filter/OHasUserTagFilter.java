/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.image.CommentField;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OHasUserTagFilter extends OntologyNode implements IQueryGenerator {

    /* cache query */
    protected Query query;

    public OHasUserTagFilter() {
        super("Has User Tag Filter");
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            query = new WildcardQuery(new Term(getLuceneField(), "*"));
        }
        return query;
    }

    @Override
    public String getLuceneField() {
        return CommentField.NAME;
    }
}