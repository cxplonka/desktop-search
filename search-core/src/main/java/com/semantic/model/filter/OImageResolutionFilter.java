/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.image.PixelSizeField;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OImageResolutionFilter extends OMinMaxFilter<Integer>{

    public OImageResolutionFilter() {
        super(Integer.class, 0, 1000000);
    }

    @Override
    public String getLuceneField() {
        return PixelSizeField.NAME;
    }
}