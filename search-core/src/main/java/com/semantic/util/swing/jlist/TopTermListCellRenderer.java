/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jlist;

import com.semantic.util.test.topterms.TopTerm;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TopTermListCellRenderer extends JLabel implements ListCellRenderer {

    private final Color startColor = new Color(192, 192, 192);
    private final Color endColor = new Color(82, 82, 82);
    private final int outerRoundRectSize = 0;
    private final int innerRoundRectSize = 0;

    public TopTermListCellRenderer() {
        super();
        setText("dummy");
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());

        Color bg = null;
        Color fg = null;

        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null
                && !dropLocation.isInsert()
                && dropLocation.getIndex() == index) {
            bg = UIManager.getColor("List.dropCellBackground");
            fg = UIManager.getColor("List.dropCellForeground");
            isSelected = true;
        }

        if (isSelected) {
            setBackground(bg == null ? list.getSelectionBackground() : bg);
            setForeground(fg == null ? list.getSelectionForeground() : fg);
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value instanceof TopTerm) {
            setText(((TopTerm) value).getTerm());
        } else {
            setText((value == null) ? "" : value.toString());
        }
        setEnabled(list.isEnabled());

        return this;
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