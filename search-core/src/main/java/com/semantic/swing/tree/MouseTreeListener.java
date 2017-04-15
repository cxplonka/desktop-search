/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree;

import com.l2fprod.common.propertysheet.Property;
import com.semantic.swing.propertysheet.IPropertySheetNode;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class MouseTreeListener extends MouseAdapter implements TreeSelectionListener {

    private JPopupMenu popup;
    private SemanticControlPanel panel;

    public MouseTreeListener(SemanticControlPanel panel) {
        this.panel = panel;
    }

    private void initActions(Action[] actions) {
        /* lazy init */
        if (popup == null) {
            popup = new JPopupMenu();
        }
        /* remove all nodes before */
        popup.removeAll();
        /* setup node actions */
        if (actions != null) {
            for (Action action : actions) {
                if (action == null) {
                    popup.addSeparator();
                } else {
                    popup.add(action);
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        /* */
        if (path != null) {
            /* user want see popup menu */
            if (SwingUtilities.isRightMouseButton(e)) {
                Action[] actions = null;
                if (path.getLastPathComponent() instanceof IActionNode) {
                    /* init action system */
                    actions = ((IActionNode) path.getLastPathComponent()).getActions();
                }
                /* */
                initActions(actions);
                /* select node and show popup */
                tree.setSelectionPath(path);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        /* update property sheet view */
        try {/* tree selection model eating nullpointerexception, lookup */
            TreePath selection = e.getNewLeadSelectionPath();
            if (selection.getLastPathComponent() instanceof IPropertySheetNode) {
                IPropertySheetNode sheetNode = (IPropertySheetNode) selection.getLastPathComponent();
                /* */
                Property[] properties = sheetNode.createPropertys();
                if (properties != null) {
                    panel.getSheetPanel().setProperties(properties);
                    panel.getOverlayUI().setVisible(false);
                } else {
                    panel.getSheetPanel().setProperties(new Property[]{});
                    panel.getOverlayUI().setVisible(true);
                }
            } else {
                panel.getSheetPanel().setProperties(new Property[]{});
                panel.getOverlayUI().setVisible(true);
            }
        } catch (Exception ea) {
        }
    }
}
