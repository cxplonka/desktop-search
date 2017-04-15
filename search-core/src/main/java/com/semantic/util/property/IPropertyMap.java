/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.property;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface IPropertyMap {

    public void addPropertyChangeListener(PropertyChangeListener l);

    public void removePropertyChangeListener(PropertyChangeListener l);

    public <T> T get(IPropertyKey<T> key);

    public <T> void set(IPropertyKey<T> key, T value);

    public <T> void remove(IPropertyKey<T> key);

    public boolean has(IPropertyKey key);
}
