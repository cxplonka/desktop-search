/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.grid;

import com.guigarage.jgrid.ui.BasicGridUI;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ImageBoxGridUI extends BasicGridUI {

    private final Rectangle viewBounds = new Rectangle();

    @Override
    protected void paintCell(Graphics g, JComponent c, int index, Rectangle bounds, int leadIndex) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(bounds.x, bounds.y);

        viewBounds.setBounds(0, 0, bounds.width, bounds.height);
        super.paintCell(g2d, c, index, viewBounds, leadIndex);
        g2d.translate(-bounds.x, -bounds.y);
    }
}