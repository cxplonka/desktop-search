/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.fields.image;

import com.semantic.lucene.util.IFieldProperty;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 * ExifIFD0 image exchange format informations
 * ExifIFD0 field TAG_MAKE (datatype - string)
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ExifMakeField implements IFieldProperty<String> {

    public static final String NAME = "image_exif_make";

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
        /* the analyzed field for searching */
        doc.add(new TextField(getName(), value, Field.Store.YES));
        /* and the stored field for suggestion */
        doc.add(new StringField(getName() + EXT_SUGGEST, value, Field.Store.YES));
    }
}
