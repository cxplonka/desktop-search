/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.semantic.model.filter;

import com.semantic.lucene.fields.FileExtField;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OCalcFilter extends OMultiTermQuery {

    public OCalcFilter() {
        super();
        setName("Office: Calc");
    }

    @Override
    public String[] getTerms() {
        return new String[]{"xls", "xlt", "xlsx", "ods", "xltx"};
    }

    @Override
    public String getLuceneField() {
        return FileExtField.NAME;
    }
}