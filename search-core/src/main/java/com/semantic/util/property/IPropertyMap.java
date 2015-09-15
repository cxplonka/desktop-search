/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.semantic.util.property;

import java.beans.PropertyChangeListener;

/**
 *
 * @author cplonka
 */
public interface IPropertyMap {

    public void addPropertyChangeListener(PropertyChangeListener l);

    public void removePropertyChangeListener(PropertyChangeListener l);

    public <T> T getProperty(IPropertyKey<T> key);

    public <T> void setProperty(IPropertyKey<T> key, T value);
    
    public <T> void removeProperty(IPropertyKey<T> key);
    
    public boolean containsProperty(IPropertyKey key);
}
