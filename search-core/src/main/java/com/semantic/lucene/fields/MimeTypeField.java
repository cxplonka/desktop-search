/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.semantic.lucene.fields;

import com.semantic.lucene.util.IFieldProperty;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

/**
 * mime type of the content (datatype - string) 
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class MimeTypeField implements IFieldProperty<String> {

    public static final String NAME = "mime_type";

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
        doc.add(new StringField(getName(), value, Field.Store.YES));
    }
}