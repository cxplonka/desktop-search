/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jlist;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
class RemoveLabel extends JLabel {

    private final Color startColor = new Color(192, 192, 192);
    private final Color endColor = new Color(82, 82, 82);
    private final int outerRoundRectSize = 0;
    private final int innerRoundRectSize = 0;
    
    public RemoveLabel(String text) {
        super(text);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        GradientPaint gp = new GradientPaint(0, 0, startColor, 0, h, endColor, true);
        g2d.setPaint(gp);

        GradientPaint p1 = new GradientPaint(0, 0, new Color(100, 100, 100), 0, h - 1,
                new Color(0, 0, 0));
        GradientPaint p2 = new GradientPaint(0, 1, new Color(255, 255, 255, 100), 0,
                h - 3, new Color(0, 0, 0, 50));


        RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0, w - 1,
                h - 1, outerRoundRectSize, outerRoundRectSize);
        Shape clip = g2d.getClip();
        g2d.clip(r2d);
        g2d.fillRect(0, 0, w, h);
        g2d.setClip(clip);
        g2d.setPaint(p1);
        g2d.drawRoundRect(0, 0, w - 1, h - 1, outerRoundRectSize,
                outerRoundRectSize);
        g2d.setPaint(p2);
        g2d.drawRoundRect(1, 1, w - 3, h - 3, innerRoundRectSize,
                innerRoundRectSize);

        super.paint(g);
    }
}