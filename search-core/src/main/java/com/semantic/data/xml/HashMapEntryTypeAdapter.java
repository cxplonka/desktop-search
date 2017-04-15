/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.data.xml;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class HashMapEntryTypeAdapter extends XmlAdapter<Object, Object> {

    private static final Map<Class, XmlAdapter> ADAPTERS =
            new HashMap<Class, XmlAdapter>();

    public static <T> void addAdapter(Class<T> clazz, XmlAdapter<?, T> adapter) {
        ADAPTERS.put(clazz, adapter);
    }

    public static <T> void removeAdapter(Class<T> clazz) {
        ADAPTERS.remove(clazz);
    }

    @Override
    public Object unmarshal(Object v) throws Exception {
        if (v instanceof XmlAdapter) {
            return ((XmlAdapter) v).unmarshal(v);
        }
        return v;
    }

    @Override
    public Object marshal(Object v) throws Exception {
        if (v != null && ADAPTERS.containsKey(v.getClass())) {
            XmlAdapter adapter = ADAPTERS.get(v.getClass());
            return adapter.marshal(v);
        }
        return v;
    }
}
