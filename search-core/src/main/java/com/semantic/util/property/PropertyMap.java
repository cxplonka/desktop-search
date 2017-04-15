/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.property;

import com.semantic.data.xml.IPropertyKeyMapAdapter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 * @author Christian Plonka (cplonka81@gmail.com)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PropertyMap extends PropertySupport implements IPropertyMap {

    @XmlElement
    @XmlJavaTypeAdapter(IPropertyKeyMapAdapter.class)
    protected HashMap<IPropertyKey, Object> properties;

    @Override
    public boolean has(IPropertyKey key) {
        if (properties != null) {
            return properties.containsKey(key);
        }
        return false;
    }

    @Override
    public <T> T get(IPropertyKey<T> key) {
        if (properties == null || !properties.containsKey(key)) {
            return key.getDefaultValue();
        }
        /* */
        return key.getClazz().cast(properties.get(key));
    }

    @Override
    public <T> void remove(IPropertyKey<T> key) {
        if (properties != null) {
            properties.remove(key);
        }
    }

    @Override
    public <T> void set(IPropertyKey<T> key, T value) {
        if (properties == null) {
            properties = new HashMap<IPropertyKey, Object>();
        }
        /* type */
        switch (key.getType()) {
            case READ_WRITE:
                firePropertyChange(key.getName(),
                        properties.put(key, value),
                        value);
                break;
            case RW_NO:
                properties.put(key, value);
                break;
        }
    }

    public Collection<IPropertyKey> getPropertyKeys() {
        if (properties != null) {
            return properties.keySet();
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void dispose() {
        super.dispose();
        /* cleanup */
        if (properties != null) {
            properties.clear();
        }
    }
}