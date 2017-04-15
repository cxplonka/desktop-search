/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jtree;

import com.jidesoft.swing.JideSwingUtilities;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class CheckBoxJTree extends JTree {

    public CheckBoxJTree() {
        super();
        Handler handler = new Handler(this);
        JideSwingUtilities.insertMouseListener(this, handler, 0);
        addKeyListener(handler);
    }

    protected boolean isCheckBoxEnabled(TreePath path) {
        return true;
    }

    protected boolean isCheckBoxVisible(TreePath path) {
        return true;
    }

    /**
     * JIDE Handler
     */
    protected static class Handler implements MouseListener, KeyListener, TreeSelectionListener {

        protected CheckBoxJTree _tree;
        int _hotspot = new JCheckBox().getPreferredSize().width;
        private int _toggleCount = -1;

        public Handler(CheckBoxJTree tree) {
            _tree = tree;
        }

        protected TreePath getTreePathForMouseEvent(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                return null;
            }

            TreePath path = _tree.getPathForLocation(e.getX(), e.getY());
            if (path == null) {
                return null;
            }

            if (clicksInCheckBox(e, path)) {
                return path;
            } else {
                return null;
            }
        }

        protected boolean clicksInCheckBox(MouseEvent e, TreePath path) {
            if (!_tree.isCheckBoxVisible(path)) {
                return false;
            } else {
                Rectangle bounds = _tree.getPathBounds(path);
                if (_tree.getComponentOrientation().isLeftToRight()) {
                    return e.getX() < bounds.x + _hotspot;
                } else {
                    return e.getX() > bounds.x + bounds.width - _hotspot;
                }
            }
        }

        private TreePath preventToggleEvent(MouseEvent e) {
            TreePath pathForMouseEvent = getTreePathForMouseEvent(e);
            if (pathForMouseEvent != null) {
                int toggleCount = _tree.getToggleClickCount();
                if (toggleCount != -1) {
                    _toggleCount = toggleCount;
                    _tree.setToggleClickCount(-1);
                }
            }
            return pathForMouseEvent;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.isConsumed()) {
                return;
            }
            preventToggleEvent(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isConsumed()) {
                return;
            }
            TreePath path = preventToggleEvent(e);
            if (path != null) {
                toggleSelections(new TreePath[]{path});
                e.consume();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isConsumed()) {
                return;
            }

            TreePath path = preventToggleEvent(e);
            if (path != null) {
                e.consume();
            }
            if (_toggleCount != -1) {
                _tree.setToggleClickCount(_toggleCount);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.isConsumed()) {
                return;
            }
            if (e.getModifiers() == 0 && e.getKeyChar() == KeyEvent.VK_SPACE) {
                toggleSelections();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            _tree.treeDidChange();
        }

        protected void toggleSelections() {
            TreePath[] treePaths = _tree.getSelectionPaths();
            toggleSelections(treePaths);
        }

        private void toggleSelections(TreePath[] treePaths) {
            if (treePaths == null || treePaths.length == 0 || !_tree.isEnabled()) {
                return;
            }
            if (treePaths.length == 1 && !_tree.isCheckBoxEnabled(treePaths[0])) {
                return;
            }            
            try {
                for (TreePath treePath : treePaths) {
                    /* delegate to tree nodes */
                    if (treePath.getLastPathComponent() instanceof AbstractOMutableTreeNode) {
                        AbstractOMutableTreeNode node = (AbstractOMutableTreeNode) treePath.getLastPathComponent();
                        node.setChecked(!node.isChecked());
                    }
                }
            } finally {
                _tree.treeDidChange();
            }
        }
    }
}