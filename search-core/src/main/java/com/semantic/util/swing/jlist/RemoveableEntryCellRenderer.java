/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jlist;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class RemoveableEntryCellRenderer extends JPanel implements ListCellRenderer {

    private final ListCellRenderer delegate;
    private final JLabel removeLabel = new RemoveLabel("x");

    public RemoveableEntryCellRenderer(ListCellRenderer renderer) {
        this.delegate = renderer;
        setLayout(new BorderLayout());
        setOpaque(false);
        removeLabel.setOpaque(false);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component renderer = delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        removeAll();        
        add(removeLabel, BorderLayout.EAST);
        add(renderer, BorderLayout.CENTER);
        return this;
    }

    public static int calculateHotSpot() {
        return new RemoveLabel("x").getPreferredSize().width;
    }
}