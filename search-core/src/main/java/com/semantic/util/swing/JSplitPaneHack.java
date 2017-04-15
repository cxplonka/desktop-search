/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class JSplitPaneHack extends JSplitPane implements ComponentListener {

    private boolean holdDivider = false;
    protected boolean m_collapseLeft = false;
    protected boolean m_collapseRight = false;
    protected boolean m_fIsPainted = false;
    protected double m_dProportionalLocation = -1;

    public JSplitPaneHack() {
        this(HORIZONTAL_SPLIT);
    }

    public JSplitPaneHack(int iOrientation) {
        super(iOrientation);
        addComponentListener(this);
    }

    public boolean hasProportionalLocation() {
        return (m_dProportionalLocation != -1);
    }

    public void cancelDividerProportionalLocation() {
        m_dProportionalLocation = -1;
    }

    @Override
    public void setDividerLocation(int location) {
        super.setDividerLocation(location);
        /* hold divider position after resize */
        if (getDividerLocationPercent() > .9) {
            holdDivider = true;
        } else {
            holdDivider = false;
        }
    }

    @Override
    public void setDividerLocation(double dProportionalLocation) {
        if (dProportionalLocation < 0 || dProportionalLocation > 1) {
            throw new IllegalArgumentException("Illegal value for divider location: " + dProportionalLocation);
        }
        m_dProportionalLocation = dProportionalLocation;
        if (m_fIsPainted) {
            super.setDividerLocation(m_dProportionalLocation);
        }
    }

    public double getDividerLocationPercent() {
        if (getOrientation() == VERTICAL_SPLIT) {
            return (double) getDividerLocation() / (getHeight() - getDividerSize());
        } else {
            return (double) getDividerLocation() / (getWidth() - getDividerSize());
        }
    }

    public void collapseLeft() {
        m_collapseLeft = true;
    }

    public void collapseRight() {
        m_collapseRight = true;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (hasProportionalLocation()) {
            super.setDividerLocation(m_dProportionalLocation);
            cancelDividerProportionalLocation();
            /* hacky collapse workaround */
            if (m_collapseLeft || m_collapseRight) {
                try {
                    BasicSplitPaneUI basicUI = (BasicSplitPaneUI) getUI();
                    JButton oneClick = (JButton) basicUI.getDivider().getComponent(
                            m_collapseLeft ? 0 : 1);
                    oneClick.doClick();
                    m_collapseRight = m_collapseLeft = false;
                } catch (Exception e) {
                    /* try this approach, make sure the component inside have minsize 0 0 */
                    if (getOrientation() == HORIZONTAL_SPLIT) {
                        setDividerLocation(m_collapseLeft ? 0 : getWidth());
                    } else {
                        setDividerLocation(m_collapseLeft ? 0 : getHeight());
                    }
                }
            }
        }
        m_fIsPainted = true;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (holdDivider) {
            if (getOrientation() == HORIZONTAL_SPLIT) {
                setDividerLocation(getWidth());
            } else {
                setDividerLocation(getHeight());
            }
            holdDivider = true;
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}