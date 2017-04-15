/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SwingUtils {

    public static void centerComponent(Component relativeTo, Component toCenter) {
        if (relativeTo == null) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension screenSize = tk.getScreenSize();
            int screenHeight = screenSize.height;
            int screenWidth = screenSize.width;
            toCenter.setLocation((screenWidth / 2) - (toCenter.getSize().width / 2),
                    (screenHeight / 2) - (toCenter.getSize().height / 2));
        } else {
            Point loc = relativeTo.getLocationOnScreen();
            Rectangle bounds = relativeTo.getBounds();
            toCenter.setLocation((int) (loc.x + bounds.getWidth() / 2) - (toCenter.getWidth() / 2),
                    (int) (loc.y + bounds.getHeight() / 2) - (toCenter.getHeight() / 2));

        }
    }

    public static void registerKeyBoardAction(JComponent comp, Action action) {
        registerKeyBoardAction(comp, action, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static void unregisterKeyBoardAction(JComponent comp, Action action) {
        unregisterKeyBoardAction(comp, action, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static void unregisterKeyBoardAction(JComponent comp, Action action, int condition) {
        comp.getInputMap(condition).remove((KeyStroke) action.getValue(
                Action.ACCELERATOR_KEY));
        comp.getActionMap().remove(action.getValue(Action.NAME));
    }

    public static void registerKeyBoardAction(JComponent comp, Action action, KeyStroke stroke) {
        comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, action.getValue(Action.NAME));
        comp.getActionMap().put(action.getValue(Action.NAME), action);
    }

    /**
     * 
     * @param comp
     * @param action
     * @param condition - see {@link JComponent}
     * (WHEN_FOCUSED, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,WHEN_IN_FOCUSED_WINDOW)
     */
    public static void registerKeyBoardAction(JComponent comp, Action action, int condition) {
        comp.getInputMap(condition).put((KeyStroke) action.getValue(
                Action.ACCELERATOR_KEY), action.getValue(Action.NAME));
        comp.getActionMap().put(action.getValue(Action.NAME), action);
    }

    /**
     * recursive implementation
     * @param value
     * @param c
     */
    public static void enable(JComponent c, boolean value) {
        Component comp[] = c.getComponents();
        for (int i = 0; i < comp.length; i++) {
            comp[i].setEnabled(value);
            if (comp[i] instanceof JComponent) {
                enable((JComponent) comp[i], value);
            }
        }
    }
}