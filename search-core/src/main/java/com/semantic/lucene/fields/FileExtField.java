/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.fields;

import com.semantic.lucene.util.IFieldProperty;
import com.semantic.util.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.facet.FacetField;

/**
 * field for file extension (datatype - string)
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FileExtField implements IFieldProperty<String> {

    public static final String NAME = "file_extension";

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void add(Document doc, String value) {
        if (!StringUtils.isEmpty(value)) {
            doc.add(new StringField(getName(), value, Field.Store.YES));
            doc.add(new FacetField(getName(), value));
        }
    }

    @Override
    public boolean hasFacet() {
        return true;
    }
}
