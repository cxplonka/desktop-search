/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.propertysheet;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import java.awt.Dimension;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class EmptyCellRenderer extends DefaultCellRenderer {

    public EmptyCellRenderer() {
        super();
        setOpaque(false);
        setPreferredSize(new Dimension(100, getFontMetrics(getFont()).getHeight() + 2));
    }

    @Override
    protected String convertToString(Object value) {
        return "";
    }
}