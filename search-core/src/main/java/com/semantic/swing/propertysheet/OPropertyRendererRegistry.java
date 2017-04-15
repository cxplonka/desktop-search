/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.propertysheet;

import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import java.io.File;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OPropertyRendererRegistry extends PropertyRendererRegistry {

    private static OPropertyRendererRegistry ref;

    private OPropertyRendererRegistry() {
        /* default generic renderer */
        registerRenderer(Object.class, new EmptyCellRenderer());
        /* */
        DefaultCellRenderer renderer = new DefaultCellRenderer();
        registerRenderer(String.class, renderer);
        registerRenderer(Number.class, renderer);
        registerRenderer(File.class, renderer);
    }    
    
    public synchronized static OPropertyRendererRegistry def() {
        if (ref == null) {
            ref = new OPropertyRendererRegistry();
        }
        return ref;
    }    
}