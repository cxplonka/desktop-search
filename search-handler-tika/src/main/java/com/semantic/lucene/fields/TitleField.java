package com.semantic.lucene.fields;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.semantic.lucene.util.IFieldProperty;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.tika.metadata.DublinCore;

/**
 * dc:title
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TitleField implements IFieldProperty<String> {

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String getName() {
        return DublinCore.TITLE.getName();
    }

    @Override
    public void add(Document doc, String value) {
        /* the analyzed field for searching */
        doc.add(new TextField(getName(), value, Field.Store.YES));
        /* and the stored field for suggestion */
        doc.add(new StringField(getName() + EXT_SUGGEST, value, Field.Store.YES));
    }
}