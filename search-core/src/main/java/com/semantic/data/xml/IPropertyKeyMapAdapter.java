/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.data.xml;

import com.semantic.util.property.JAXBKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class IPropertyKeyMapAdapter extends XmlAdapter<HashMapEntryType[], HashMap> {

    @Override
    public HashMap unmarshal(HashMapEntryType[] v) throws Exception {
        if (v != null) {
            HashMap ret = new HashMap(v.length);
            for (HashMapEntryType type : v) {
                ret.put(type.key, type.value);
            }
            return ret;
        }
        return null;
    }

    @Override
    public HashMapEntryType[] marshal(HashMap v) throws Exception {
        if (v != null) {
            List<HashMapEntryType> entries = new ArrayList<HashMapEntryType>();
            for (Object key : v.keySet()) {
                /* only persist jaxb property keys */
                if (key instanceof JAXBKey) {
                    entries.add(new HashMapEntryType(key, v.get(key)));
                }
            }
            return entries.isEmpty() ? null
                    : entries.toArray(new HashMapEntryType[entries.size()]);
        }
        return null;
    }
}