/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.SizeField;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OFileSizeFilter extends OMinMaxFilter<Long>{

    public OFileSizeFilter() {
        super(Long.class, 0l, 1048576l);
    }
    
    @Override
    public String getLuceneField() {
        return SizeField.NAME;
    }    
}