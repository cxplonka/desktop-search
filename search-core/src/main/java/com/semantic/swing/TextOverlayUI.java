/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TextOverlayUI extends AbstractLayerUI<JComponent> {

    private boolean visible = true;
    private JLabel label = new JLabel();

    public TextOverlayUI() {
        super();
        label.setForeground(Color.GRAY);
        label.setHorizontalAlignment(JLabel.CENTER);
    }

    public TextOverlayUI(String text) {
        this();
        label.setText(text);
    }

    public JLabel getLabel() {
        return label;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public void paint(Graphics grphcs, JComponent jc) {
        super.paint(grphcs, jc);
        if (visible) {
            label.setSize(jc.getSize());
            label.paint(grphcs);
        }
    }
}