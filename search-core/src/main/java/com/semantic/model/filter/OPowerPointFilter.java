/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.FileExtField;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OPowerPointFilter extends OMultiTermQuery {

    public OPowerPointFilter() {
        super();
        setName("Office: PowerPoint");
    }

    @Override
    public String[] getTerms() {
        return new String[]{"ppt", "pptx", "pptm", "odp", "otp"};
    }

    @Override
    public String getLuceneField() {
        return FileExtField.NAME;
    }
}
