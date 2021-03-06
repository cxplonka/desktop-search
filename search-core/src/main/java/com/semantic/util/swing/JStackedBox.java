/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.semantic.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.VerticalLayout;

/**
 * Stacks components vertically in boxes. Each box is created with a title and a
 * component.<br>
 *
 * <p>
 * The <code>JStackedBox</code> can be added to a
 * {@link javax.swing.JScrollPane}.
 *
 * <p>
 * Note: this class is not part of the SwingX core classes. It is just an
 * example of what can be achieved with the components.
 *
 * @author <a href="mailto:fred@L2FProd.com">Frederic Lavigne</a>
 */
public class JStackedBox extends JPanel implements Scrollable {

    private Color titleBackgroundColor;
    private Color titleForegroundColor;
    private Color separatorColor;
    private Border separatorBorder;

    public JStackedBox() {
        super(new VerticalLayout());
        separatorBorder = new SeparatorBorder();
        setTitleForegroundColor(Color.BLACK);
    }

    public Color getSeparatorColor() {
        return separatorColor;
    }

    public void setSeparatorColor(Color separatorColor) {
        this.separatorColor = separatorColor;
    }

    public Color getTitleForegroundColor() {
        return titleForegroundColor;
    }

    public void setTitleForegroundColor(Color titleForegroundColor) {
        this.titleForegroundColor = titleForegroundColor;
    }

    public Color getTitleBackgroundColor() {
        return titleBackgroundColor;
    }

    public void setTitleBackgroundColor(Color titleBackgroundColor) {
        this.titleBackgroundColor = titleBackgroundColor;
    }

    public JXCollapsiblePane addBoxStyle(String title, Component component) {
        final JXCollapsiblePane collapsible = new JXCollapsiblePane();
        collapsible.add(component);

        Action toggleAction = collapsible.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION);
        // use the collapse/expand icons from the JTree UI
        toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON, UIManager.getIcon("Tree.expandedIcon"));
        toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON, UIManager.getIcon("Tree.collapsedIcon"));

        JXHyperlink link = new JXHyperlink(toggleAction) {

            @Override
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;

                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(255, 255, 255), 0, getHeight(),
                        new Color(229, 229, 229));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(180, 180, 180));
                g2d.drawLine(0, 0, getWidth(), 0);
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

                super.paint(g);
            }
        };
        link.setText(title);
        setTitleForegroundColor(new Color(150, 150, 150));
        link.setFont(link.getFont().deriveFont(Font.BOLD, 18));
        link.setBackground(getTitleBackgroundColor());
        link.setFocusPainted(false);

        link.setUnclickedColor(getTitleForegroundColor());
        link.setClickedColor(getTitleForegroundColor());

        link.setBorder(new CompoundBorder(separatorBorder, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        link.setBorderPainted(true);

        add(link);
        add(collapsible);

        return collapsible;
    }

    /**
     * Adds a new component to this <code>JStackedBox</code>
     *
     * @param title
     * @param component
     */
    public JXCollapsiblePane addBox(String title, Component component) {
        final JXCollapsiblePane collapsible = new JXCollapsiblePane();
        collapsible.add(component);

        Action toggleAction = collapsible.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION);
        // use the collapse/expand icons from the JTree UI
        toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON, UIManager.getIcon("Tree.expandedIcon"));
        toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON, UIManager.getIcon("Tree.collapsedIcon"));

        JXHyperlink link = new JXHyperlink(toggleAction);
        link.setText(title);
        link.setFont(link.getFont().deriveFont(Font.BOLD));
        link.setBackground(getTitleBackgroundColor());
        link.setFocusPainted(false);

        link.setUnclickedColor(getTitleForegroundColor());
        link.setClickedColor(getTitleForegroundColor());

        link.setBorder(new CompoundBorder(separatorBorder, BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        link.setBorderPainted(true);

        add(link);
        add(collapsible);

        return collapsible;
    }

    /**
     * @see Scrollable#getPreferredScrollableViewportSize()
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * @see Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        return 10;
    }

    /**
     * @see Scrollable#getScrollableTracksViewportHeight()
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        } else {
            return false;
        }
    }

    /**
     * @see Scrollable#getScrollableTracksViewportWidth()
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * @see Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
            int direction) {
        return 10;
    }

    /**
     * The border between the stack components. It separates each component with a
     * fine line border.
     */
    class SeparatorBorder implements Border {

        boolean isFirst(Component c) {
            return c.getParent() == null || c.getParent().getComponent(0) == c;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // if the collapsible is collapsed, we do not want its border to be
            // painted.
            if (c instanceof JXCollapsiblePane) {
                if (((JXCollapsiblePane) c).isCollapsed()) {
                    return new Insets(0, 0, 0,
                            0);
                }
            }
            return new Insets(isFirst(c) ? 4 : 1, 0, 1, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width,
                int height) {
//            g.setColor(getSeparatorColor());
//            if (isFirst(c)) {
//                g.drawLine(x, y + 2, x + width, y + 2);
//            }
//            g.drawLine(x, y + height - 1, x + width, y + height - 1);
        }
    }
}