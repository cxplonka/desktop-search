/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.property;

import com.semantic.util.property.IPropertyKey.Type;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.NONE)
public class JAXBKey<T> implements IPropertyKey<T> {

    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected Class<T> type;
    @XmlTransient
    protected T defaultValue;
    /* default property type */
    private Type propertyType = Type.READ_WRITE;

    public JAXBKey() {
    }

    protected JAXBKey(String name, Class<T> type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }
    
    protected JAXBKey(String name, Class<T> type, T defaultValue, Type propertyType) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.propertyType = propertyType;
    }

    public static <T> JAXBKey<T> create(String name, Class<T> clazz, T defaultValue) {
        return new JAXBKey(name, clazz, defaultValue, Type.READ_WRITE);
    }
    
    public static <T> JAXBKey<T> createWithOut(String name, Class<T> clazz, T defaultValue) {
        return new JAXBKey(name, clazz, defaultValue, Type.RW_NO);
    }

    @Override
    public Class<T> getClazz() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Type getType() {
        return propertyType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JAXBKey<T> other = (JAXBKey<T>) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 31 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}