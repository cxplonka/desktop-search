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
public class OWordFilter extends OMultiTermQuery {

    public OWordFilter() {
        super();
        setName("Office: Word");
    }

    @Override
    public String[] getTerms() {
        return new String[]{"rtf", "doc", "docx", "odt", "ott"};
    }

    @Override
    public String getLuceneField() {
        return FileExtField.NAME;
    }
}