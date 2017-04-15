/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.plugin;

import com.semantic.util.property.IPropertyKey;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class PlugInManager extends SimpleLookup implements IPlugIn {

    @Override
    public void init(Context context) throws Exception {
        for (IPlugIn plugin : allInstances(IPlugIn.class)) {
            try {
                plugin.init(context);
                log.log(Level.INFO, String.format("init plugin - %s", plugin.getClass()));
            } catch (Throwable ex) {
                log.log(Level.INFO, "can not start plugin!", ex);
            }
        }
    }

    @Override
    public void shutdown(Context context) throws Exception {
        for (IPlugIn plugin : allInstances(IPlugIn.class)) {
            try {
                plugin.shutdown(context);
                log.log(Level.INFO, String.format("stop plugin - %s", plugin.getClass()));
            } catch (Throwable ex) {
                log.log(Level.INFO, "Can not stop plugin!", ex);
            }
        }
        dispose();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
    }

    @Override
    public <T> T get(IPropertyKey<T> key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void set(IPropertyKey<T> key, T value) {
    }

    @Override
    public <T> void remove(IPropertyKey<T> key) {
    }

    @Override
    public boolean has(IPropertyKey key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}