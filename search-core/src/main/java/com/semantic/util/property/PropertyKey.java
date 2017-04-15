/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.property;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class PropertyKey<T> implements IPropertyKey<T> {

    protected String name;
    protected Class<T> type;
    protected T defaultValue;
    /* default property type */
    private Type propertyType = Type.READ_WRITE;

    public PropertyKey() {
    }

    protected PropertyKey(String name, Class<T> type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    protected PropertyKey(String name, Class<T> type, T defaultValue, Type propertyType) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.propertyType = propertyType;
    }

    public static <T> PropertyKey<T> create(String name, Class<T> clazz, T defaultValue) {
        return new PropertyKey(name, clazz, defaultValue, Type.READ_WRITE);
    }

    public static <T> PropertyKey<T> createWithOut(String name, Class<T> clazz, T defaultValue) {
        return new PropertyKey(name, clazz, defaultValue, Type.RW_NO);
    }

    public static <T> PropertyKey<T> readOnly(String name, Class<T> clazz, T defaultValue) {
        return new PropertyKey(name, clazz, defaultValue, Type.READ);
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
        final PropertyKey<T> other = (PropertyKey<T>) obj;
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