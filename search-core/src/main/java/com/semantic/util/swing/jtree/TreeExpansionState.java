/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jtree;

import com.jidesoft.swing.CheckBoxTree;
import com.jidesoft.swing.CheckBoxTreeSelectionModel;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import com.semantic.util.property.IPropertyKey;
import com.semantic.util.property.JAXBKey;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class TreeExpansionState {

    public static final IPropertyKey<Boolean> TREE_NODE_EXPANDED = JAXBKey.create("expanded", Boolean.class, false);
    public static final IPropertyKey<Boolean> TREE_NODE_CHECKED = JAXBKey.create("checked", Boolean.class, false);
    /* */
    private final CheckBoxTree tree;

    public TreeExpansionState(CheckBoxTree tree) {
        this.tree = tree;
    }

    public void store() {
        /* store states */
        TreeModel model = tree.getModel();
        storeTreeStates(model, tree.getCheckBoxTreeSelectionModel(), (AbstractOMutableTreeNode) model.getRoot());
    }

    private void storeTreeStates(TreeModel model, CheckBoxTreeSelectionModel cmodel, AbstractOMutableTreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            AbstractOMutableTreeNode child = (AbstractOMutableTreeNode) node.getChildAt(i);
            TreePath path = new TreePath(child.getPath());
            if (!model.isLeaf(child) && tree.isExpanded(path)) {
                child.getUserObject().set(TREE_NODE_EXPANDED, true);
            } else {
                child.getUserObject().remove(TREE_NODE_EXPANDED);
            }
            /* is checked */
            if (cmodel.isPathSelected(path, true)) {
                child.getUserObject().set(TREE_NODE_CHECKED, true);
            } else {
                child.getUserObject().remove(TREE_NODE_CHECKED);
            }
            /* depther */
            storeTreeStates(model, cmodel, child);
        }
    }

    private void restoreTreeStates(TreeModel model, AbstractOMutableTreeNode node, List<TreePath> paths) {
        for (int i = 0; i < node.getChildCount(); i++) {
            AbstractOMutableTreeNode child = (AbstractOMutableTreeNode) node.getChildAt(i);
            TreePath path = new TreePath(child.getPath());
            /* check */
            if (child.getUserObject().get(TREE_NODE_CHECKED)) {                
                paths.add(path);
            }
            /* expand */
            if (child.getUserObject().get(TREE_NODE_EXPANDED)) {
                tree.expandPath(path);                
            }
            /* depther */
            restoreTreeStates(model, child, paths);
        }
    }

    public void restore() {
        try {
            /* restore states */
            List<TreePath> paths = new ArrayList<TreePath>();
            TreeModel model = tree.getModel();
            restoreTreeStates(model, (AbstractOMutableTreeNode) model.getRoot(), paths);
            /* */
            tree.getCheckBoxTreeSelectionModel().setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
        } catch (Exception e) {
        }
    }
}