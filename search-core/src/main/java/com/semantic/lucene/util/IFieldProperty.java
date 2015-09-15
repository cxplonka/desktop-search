/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.util;

import org.apache.lucene.document.Document;

/**
 *
 * @author Christian
 * @param <T>
 */
public interface IFieldProperty<T> {

    static final String EXT_SUGGEST = "_suggest";
    
    public Class<T> getClazz();

    /**
     * name of the field
     * @return 
     */
    public String getName();
    
    /**
     * add the value to the document
     * @param doc
     * @param value 
     */
    public void add(Document doc, T value);
}