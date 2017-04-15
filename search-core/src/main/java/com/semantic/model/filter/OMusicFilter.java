/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.FileExtField;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OMusicFilter extends OntologyNode implements IQueryGenerator {

    /* cache query */
    protected Query query;

    public OMusicFilter() {
        super("Music Filter");
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            BooleanQuery.Builder root = new BooleanQuery.Builder();
            root.add(new TermQuery(new Term(getLuceneField(), "mp3")), Occur.SHOULD);
            root.add(new TermQuery(new Term(getLuceneField(), "wav")), Occur.SHOULD);
            root.add(new TermQuery(new Term(getLuceneField(), "pcm")), Occur.SHOULD);
            query = root.build();
        }
        return query;
    }

    @Override
    public String getLuceneField() {
        return FileExtField.NAME;
    }
}