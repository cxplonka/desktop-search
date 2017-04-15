/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.data.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
@XmlRootElement
public class HashMapEntryType {

    @XmlElement
    public Object key;
    
    @XmlElement
    @XmlJavaTypeAdapter(HashMapEntryTypeAdapter.class)
    public Object value;

    public HashMapEntryType() {
    }    

    public HashMapEntryType(Object key, Object value) {
        this.key = key;
        this.value = value;
    }
}
