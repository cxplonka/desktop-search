/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.image.LatField;
import com.semantic.lucene.fields.image.LonField;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author cplonka
 */
public class OHasGPSFilter extends OntologyNode implements IQueryGenerator {

    /* cache query */
    protected Query query;

    public OHasGPSFilter() {
        super("Has User GPS Filter");
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            /* degree offset */
            double offset = 180;
            /* root query */
            BooleanQuery root = new BooleanQuery();
            root.add(NumericRangeQuery.newDoubleRange(LatField.NAME,
                    0 - offset, 0 + offset, true, true), Occur.MUST);
            root.add(NumericRangeQuery.newDoubleRange(LonField.NAME,
                    0 - offset, 0 + offset, true, true), Occur.MUST);
            query = root;
        }
        return query;
    }

    @Override
    public String getLuceneField() {
        return LatField.NAME;
    }
}