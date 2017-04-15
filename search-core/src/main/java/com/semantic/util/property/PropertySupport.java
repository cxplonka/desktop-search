/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.property;

import com.semantic.util.Disposable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class PropertySupport implements Disposable {

    protected PropertyChangeSupport propertyChangeSupport;

    public void addPropertyChangeListener(String property, PropertyChangeListener l) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(property, l);
    }

    public void removePropertyChangeListener(String property, PropertyChangeListener l) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.removePropertyChangeListener(property, l);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.removePropertyChangeListener(l);
        }
    }

    public void firePropertyChange(PropertyChangeEvent evt) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.firePropertyChange(evt);
        }
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyChangeSupport != null) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * removes all listeners
     */
    @Override
    public void dispose() {
        if (propertyChangeSupport != null) {
            for (PropertyChangeListener l : propertyChangeSupport.getPropertyChangeListeners()) {
                propertyChangeSupport.removePropertyChangeListener(l);
            }
        }
    }
}
