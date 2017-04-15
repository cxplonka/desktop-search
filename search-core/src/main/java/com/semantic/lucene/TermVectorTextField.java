/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene;

import java.io.Reader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TermVectorTextField extends Field {

    /* Indexed, tokenized, not stored. */
    public static final FieldType TYPE_NOT_STORED = new FieldType();

    /* Indexed, tokenized, stored. */
    public static final FieldType TYPE_STORED = new FieldType();

    static {
//        TYPE_NOT_STORED.setIndexed(true);
        TYPE_NOT_STORED.setTokenized(true);
        TYPE_NOT_STORED.freeze();

//        TYPE_STORED.setIndexed(true);
        TYPE_STORED.setTokenized(true);
        TYPE_STORED.setStored(true);
        TYPE_STORED.setStoreTermVectors(true);
        TYPE_STORED.setStoreTermVectorPositions(true);
        TYPE_STORED.setStoreTermVectorOffsets(true);
        TYPE_STORED.freeze();
    }

    public TermVectorTextField(String name, Reader reader, Store store) {
        super(name, reader, store == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
    }

    public TermVectorTextField(String name, String value, Store store) {
        super(name, value, store == Store.YES ? TYPE_STORED : TYPE_NOT_STORED);
    }

    public TermVectorTextField(String name, TokenStream stream) {
        super(name, stream, TYPE_NOT_STORED);
    }
}