/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.plaf.basic.BasicTreeUI.NodeDimensionsHandler;
import javax.swing.tree.AbstractLayoutCache;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class WrapTreeUI extends BasicTreeUI {

    private TreeUI _delegate;

    public WrapTreeUI(TreeUI delegate) {
        this._delegate = delegate;        
    }    
    
    @Override
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
        return new NodeDimensionsHandler() {

            @Override
            public Rectangle getNodeDimensions(
                    Object value, int row, int depth, boolean expanded, Rectangle size) {

                Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
                int containerWidth = tree.getParent() instanceof JViewport
                        ? tree.getParent().getWidth() : tree.getWidth();
                /* take care border */
                if (tree.getBorder() != null) {
                    Insets insets = tree.getBorder().getBorderInsets(tree);
                    containerWidth -= insets.left + insets.right;
                }
                dimensions.width = containerWidth - getRowX(row, depth);
                
                return dimensions;
            }
        };
    }
    
    public void paint(Graphics g, JComponent c) {
        _delegate.paint(g, c);
    }

    public String toString() {
        return super.toString() + "[delegate=" + _delegate + "]";
    }

    public void installUI(javax.swing.JComponent c) {
        _delegate.installUI(c);
    }

    public void uninstallUI(javax.swing.JComponent c) {
        _delegate.uninstallUI(c);
    }

    public java.awt.Dimension getPreferredSize(javax.swing.JComponent c) {
        return _delegate.getPreferredSize(c);
    }

    public java.awt.Dimension getMinimumSize(javax.swing.JComponent c) {
        return _delegate.getMinimumSize(c);
    }

    public java.awt.Dimension getMaximumSize(javax.swing.JComponent c) {
        return _delegate.getMaximumSize(c);
    }

    public boolean contains(javax.swing.JComponent c, int x, int y) {
        return _delegate.contains(c, x, y);
    }

    public int getAccessibleChildrenCount(javax.swing.JComponent c) {
        return _delegate.getAccessibleChildrenCount(c);
    }

    public javax.accessibility.Accessible getAccessibleChild(javax.swing.JComponent c, int i) {
        return _delegate.getAccessibleChild(c, i);
    }

    public java.awt.Rectangle getPathBounds(javax.swing.JTree tree, javax.swing.tree.TreePath path) {        
        return _delegate.getPathBounds(tree, path);
    }

    public javax.swing.tree.TreePath getPathForRow(javax.swing.JTree tree, int row) {
        return _delegate.getPathForRow(tree, row);
    }

    public int getRowForPath(javax.swing.JTree tree, javax.swing.tree.TreePath path) {
        return _delegate.getRowForPath(tree, path);
    }

    public int getRowCount(javax.swing.JTree tree) {
        return _delegate.getRowCount(tree);
    }

    public javax.swing.tree.TreePath getClosestPathForLocation(javax.swing.JTree tree, int x, int y) {
        return _delegate.getClosestPathForLocation(tree, x, y);
    }

    public boolean isEditing(javax.swing.JTree tree) {
        return _delegate.isEditing(tree);
    }

    public boolean stopEditing(javax.swing.JTree tree) {
        return _delegate.stopEditing(tree);
    }

    public void cancelEditing(javax.swing.JTree tree) {
        _delegate.cancelEditing(tree);
    }

    public void startEditingAtPath(javax.swing.JTree tree, javax.swing.tree.TreePath path) {
        _delegate.startEditingAtPath(tree, path);
    }

    public javax.swing.tree.TreePath getEditingPath(javax.swing.JTree tree) {
        return _delegate.getEditingPath(tree);
    }    
}