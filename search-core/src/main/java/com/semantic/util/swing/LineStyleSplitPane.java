/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LineStyleSplitPane extends JSplitPaneHack {

    public LineStyleSplitPane() {
        this(HORIZONTAL_SPLIT);
    }

    public LineStyleSplitPane(int orientation) {
        super(orientation);
        initComponents();
    }

    private void initComponents() {
        setContinuousLayout(true);
        setDividerSize(1);
        ((BasicSplitPaneUI) getUI()).getDivider().setBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));
        setBorder(BorderFactory.createEmptyBorder());
    }
}