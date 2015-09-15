/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * http://www.jroller.com/santhosh/entry/visual_clues_for_jtree_dnd
 */
public class MoveNodeDropTarget extends TreeDropListener {

    private DropTarget target;

    public MoveNodeDropTarget(JTree tree) {
        super();
        this.target = new DropTarget(tree, this);
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        super.drop(dtde);
        if (treePath == null) {
            dtde.rejectDrop();
            return;
        }

        JTree tree = (JTree) dtde.getDropTargetContext().getComponent();
//        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        try {
            String data = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
            DefaultMutableTreeNode dragNode = new DefaultMutableTreeNode(data);
            DefaultMutableTreeNode dropNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            DefaultMutableTreeNode dropParent = (DefaultMutableTreeNode) dropNode.getParent();
            if (dropParent == null) {
                dtde.rejectDrop();
                return;
            }
//            int dropIndex = treeModel.getIndexOfChild(dropParent, dropNode);
//            if (before == null) {
//                treeModel.removeNodeFromParent(dropNode);
//                treeModel.insertNodeInto(dragNode, dropParent, dropIndex);
//            } else if (before.equals(Boolean.TRUE)) {
//                treeModel.insertNodeInto(dragNode, dropParent, dropIndex);
//            } else {
//                if (dropIndex < dropParent.getChildCount()) {
//                    treeModel.insertNodeInto(dragNode, dropParent, dropIndex + 1);
//                } else {
//                    dropParent.add(dragNode);
//                    treeModel.nodesWereInserted(dropParent, new int[dropIndex + 1]);
//                }
//            }
            tree.setSelectionPath(new TreePath(dragNode.getPath()));
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}