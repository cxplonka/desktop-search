/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.plugin;

import com.semantic.util.property.IPropertyKey;
import com.semantic.util.property.IPropertyMap;
import com.semantic.util.property.PropertyKey;
import java.util.logging.Logger;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <T>
 */
public interface IPlugIn<T extends Context> extends IPropertyMap {

    public static final Logger log = Logger.getLogger(IPlugIn.class.getName());
    /* */
    public static final IPropertyKey<String> KEY_NAME
            = PropertyKey.create("plugin_name", String.class, "PlugIn");
    /* */
    public static final IPropertyKey<String> KEY_DESCRIPTION
            = PropertyKey.create("plugin_description", String.class, "Description");

    public abstract void init(T context) throws Exception;

    public abstract void shutdown(T context) throws Exception;
}
