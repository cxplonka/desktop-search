/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jlist;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class RemoveableListManager extends MouseAdapter {

    private final JList list;
    int hotspot = RemoveableEntryCellRenderer.calculateHotSpot();

    public RemoveableListManager(JList list) {
        this.list = list;
        list.setCellRenderer(new RemoveableEntryCellRenderer(list.getCellRenderer()));
        list.addMouseListener(this);
    }

    private void removeEntry(int index) {
        if (index < 0) {
            return;
        }
        /* only support defaultlistmodel */
        if (list.getModel() instanceof DefaultListModel) {
            ((DefaultListModel) list.getModel()).remove(index);
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int index = list.locationToIndex(me.getPoint());
        if (index < 0) {
            return;
        }
        double maxX = list.getCellBounds(index, index).getMaxX();
        if (me.getX() > maxX - hotspot && me.getX() < maxX) {
            removeEntry(index);
        }
    }
}