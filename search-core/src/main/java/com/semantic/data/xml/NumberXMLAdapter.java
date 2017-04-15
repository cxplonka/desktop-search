/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.data.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class NumberXMLAdapter extends XmlAdapter<String, Number> {

    @Override
    public Number unmarshal(String v) throws Exception {
        /* best/correct way!? */
        return Double.parseDouble(v);
    }

    @Override
    public String marshal(Number v) throws Exception {
        return v.toString();
    }
}