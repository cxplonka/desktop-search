/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.property;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface IPropertyKey<T> {

    public static enum Type {

        /**
         * property is read- and writeable and NOT fired an
         * property change event after modification
         */
        RW_NO,
        /**
         * property is read- and writeable and fired an
         * property change event after modification
         */
        READ_WRITE,
        /**
         * property can only be read
         */
        READ
    }

    public Class<T> getClazz();

    public String getName();

    public T getDefaultValue();

    public Type getType();
}