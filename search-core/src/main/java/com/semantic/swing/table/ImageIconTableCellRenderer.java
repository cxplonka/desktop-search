/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.table;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ImageIconTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof ImageIcon) {
            setIcon((ImageIcon) value);
        }
        return this;
    }
}
