/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.util;

import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <T>
 */
public interface IFieldProperty<T> {

    static final String EXT_SUGGEST = "_suggest";

    Class<T> getType();

    /**
     * name of the field
     * @return
     */
    String getName();

    /**
     * add the value to the document
     * @param doc
     * @param value
     */
    void add(Document doc, T value);

    /**
     * Used for TaxonomyIndex, facet search.
     * @return
     */
    default boolean isHierachical() {
        return false;
    }

    /**
     * Used for TaxonomyIndex, facet search.
     * @return
     */
    default boolean hasFacet() {
        return false;
    }

    default T get(Document doc) {
        switch (getType().getName()) {
            case "java.lang.Byte":
                return getType().cast(doc.getField(getName()).numericValue().byteValue());
            case "java.lang.Integer":
                return getType().cast(doc.getField(getName()).numericValue().intValue());
            case "java.lang.Long":
                return getType().cast(doc.getField(getName()).numericValue().longValue());
            case "java.lang.Float":
                return getType().cast(doc.getField(getName()).numericValue().floatValue());
            case "java.lang.Double":
                return getType().cast(doc.getField(getName()).numericValue().doubleValue());
            case "java.lang.String":
                return getType().cast(doc.getField(getName()).stringValue());
        }
        return null;
    }
}
