/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.fields.image;

import com.semantic.lucene.util.IFieldProperty;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;

/**
 * ExifIFD0 field TAG_DATETIME (datatype - long)
 *
 * @author Christian
 */
public class ExifDateField implements IFieldProperty<Long> {

    public static final String NAME = "image_exif_datetime";

    @Override
    public Class<Long> getClazz() {
        return Long.class;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void add(Document doc, Long value) {
        doc.add(new LongPoint(getName(), value));
    }
}
