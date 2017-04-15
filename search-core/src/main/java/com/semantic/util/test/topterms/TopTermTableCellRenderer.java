/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test.topterms;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TopTermTableCellRenderer extends DefaultTableCellRenderer implements MouseMotionListener {

    private int row = -1;
    private int col = -1;
    private boolean isRollover = false;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        String str = value != null ? value.toString() : "";

        if (!table.isEditing() && this.row == row && this.col == column && this.isRollover) {
            setText("<html><u><font color='blue'>" + str);
        } else if (hasFocus) {
            setText("<html><font color='blue'>" + str);
        } else {
            setText(str);
        }
        return this;
    }

    private static boolean isTopTermColumn(JTable table, int column) {
        return column >= 0 && table.getColumnClass(column).equals(TopTerm.class);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        JTable table = (JTable) e.getSource();
        Point pt = e.getPoint();
        int prev_row = row;
        int prev_col = col;
        boolean prev_ro = isRollover;
        row = table.rowAtPoint(pt);
        col = table.columnAtPoint(pt);
        isRollover = isTopTermColumn(table, col);
        if ((row == prev_row && col == prev_col && isRollover == prev_ro) || (!isRollover && !prev_ro)) {
            return;
        }

        Rectangle repaintRect;
        if (isRollover) {
            Rectangle r = table.getCellRect(row, col, false);
            repaintRect = prev_ro ? r.union(table.getCellRect(prev_row, prev_col, false)) : r;
        } else {
            repaintRect = table.getCellRect(prev_row, prev_col, false);
        }
        table.repaint(repaintRect);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }
}