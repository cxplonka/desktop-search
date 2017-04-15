/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TermVectorStringField extends Field {

    /** Indexed, not tokenized, omits norms, indexes DOCS_ONLY, not stored. */
    public static final FieldType TYPE_NOT_STORED = new FieldType();
    /** Indexed, not tokenized, omits norms, indexes DOCS_ONLY, stored */
    public static final FieldType TYPE_STORED = new FieldType();

    static {
//        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setOmitNorms(true);
        TYPE_NOT_STORED.setIndexOptions(IndexOptions.DOCS);
        TYPE_NOT_STORED.setTokenized(false);
        TYPE_NOT_STORED.freeze();

//        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorPositions(true);
        TYPE_STORED.setStoreTermVectorOffsets(true);
        TYPE_STORED.setOmitNorms(true);
        TYPE_STORED.setIndexOptions(IndexOptions.DOCS);
        TYPE_STORED.setStored(true);
        TYPE_STORED.setTokenized(false);
        TYPE_STORED.freeze();
    }

    public TermVectorStringField(String name, String value, Store stored) {
        super(name, value, stored == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
    }

    @Override
    public String stringValue() {
        return (fieldsData == null) ? null : fieldsData.toString();
    }
}