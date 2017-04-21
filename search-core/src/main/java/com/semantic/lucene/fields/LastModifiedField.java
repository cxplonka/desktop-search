/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.fields;

import com.semantic.lucene.util.IFieldProperty;
import java.util.Calendar;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.facet.FacetField;

/**
 * last modification date (datatype - long)
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LastModifiedField implements IFieldProperty<Long> {

    public static final String NAME = "last_modified";

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void add(Document doc, Long value) {
        doc.add(new LongPoint(getName(), value));
        doc.add(new StoredField(getName(), value));
        // for faceting
        addFacetField(doc, value);
        // for sorting
        doc.add(new NumericDocValuesField(getName(), value));
    }

    void addFacetField(Document doc, Long value) {
        /* take date */
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(value);
        /* create facet category */
        doc.add(new FacetField(getName(),
                Integer.toString(cal.get(Calendar.YEAR)),
                Integer.toString(cal.get(Calendar.MONTH))));
    }

    @Override
    public boolean hasFacet() {
        return true;
    }

    @Override
    public boolean isHierachical() {
        return true;
    }
}
