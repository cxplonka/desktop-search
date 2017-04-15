/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.dnd;

import com.semantic.swing.tree.IDropAllowed;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import javax.swing.JTree;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * http://www.jroller.com/santhosh/entry/visual_clues_for_jtree_dnd
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OTreeTransferHandler extends AbstractTreeTransferHandler {

    public OTreeTransferHandler(JTree tree) {
        super(tree, false);
    }

    @Override
    public boolean dragAllow(JTree tree, MutableTreeNode draggedNode, MutableTreeNode newParentNode) {
        if (draggedNode instanceof AbstractOMutableTreeNode) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDropPossible(JTree target, MutableTreeNode draggedNode, int action, Point location) {
        /* copy not supported */
        MutableTreeNode parentNode = dragTarget(target, draggedNode, action, location);
        if (action == DnDConstants.ACTION_MOVE && parentNode != null) {
            /* if root node or same parent or if parent is descendant then not allow drop */
            if (((AbstractOMutableTreeNode) draggedNode).isNodeDescendant(
                    (AbstractOMutableTreeNode) parentNode)) {
                return false;
            } else if (parentNode instanceof IDropAllowed) {
                return ((IDropAllowed) parentNode).isDropAllowed((AbstractOMutableTreeNode) draggedNode);
            }
        }
        return false;
    }

    @Override
    public boolean dropAllow(JTree tree, MutableTreeNode draggedNode, MutableTreeNode newParentNode, int action, Point location) {
        if (action == DnDConstants.ACTION_MOVE) {
            AbstractOMutableTreeNode from = (AbstractOMutableTreeNode) draggedNode;
            AbstractOMutableTreeNode to = (AbstractOMutableTreeNode) newParentNode;
            /* node under cursor, change position with him */
            TreePath pathTarget = tree.getPathForLocation(location.x, location.y);
            int idx = -1;
            /* get the index of the current path under the cursor */
            if (pathTarget != null) {
                MutableTreeNode node = (MutableTreeNode) pathTarget.getLastPathComponent();
                idx = to.getIndex(node);
            }
            /* redirect change to the background model, first remove from parent */
            from.getUserObject().removeFromParent();
            /* then add to new parent */
            if (idx == -1) {
                to.getUserObject().addNode(from.getUserObject());
            }else{
                to.getUserObject().insertNode(idx, from.getUserObject());
            }
            return true;
        }
        return false;
    }

    @Override
    public MutableTreeNode dragTarget(JTree target, MutableTreeNode draggedNode, int action, Point location) {
        TreePath pathTarget = target.getPathForLocation(location.x, location.y);
        if (pathTarget != null) {
            /* the parent node of the node under the cursor */
            return (MutableTreeNode) ((MutableTreeNode) pathTarget.getLastPathComponent()).getParent();
        }
        return null;
    }
}