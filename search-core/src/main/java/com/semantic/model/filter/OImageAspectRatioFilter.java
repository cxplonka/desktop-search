/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.image.AspectRatioField;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OImageAspectRatioFilter extends OMinMaxFilter<Float> {

    public OImageAspectRatioFilter() {
        super(Float.class, 0f, 1f);
    }

    @Override
    public String getLuceneField() {
        return AspectRatioField.NAME;
    }
}