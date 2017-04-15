/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.fields.image;

import com.semantic.lucene.util.IFieldProperty;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

/**
 * FIELD_USER_COMMENT in exif jpeg data (datatype - string)
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class CommentField implements IFieldProperty<String> {

    public static final String NAME = "image_user_comment";

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
        doc.add(new TextField(getName(), value, Field.Store.YES));
    }
}
