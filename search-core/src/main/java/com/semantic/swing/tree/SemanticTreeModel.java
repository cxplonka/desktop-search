/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree;

import com.semantic.model.OntologyNode;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import com.semantic.swing.tree.nodes.TreeNodeFactory;
import com.semantic.util.Disposable;
import com.semantic.util.swing.jtree.AbstractTreeTableModel;
import com.semantic.util.swing.jtree.TreeModelSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <T>
 */
public class SemanticTreeModel<T extends AbstractOMutableTreeNode> extends AbstractTreeTableModel
        implements PropertyChangeListener, Disposable {

    public SemanticTreeModel(T root) {
        super();
        /* the root */
        setRoot(root);
    }

    @Override
    public T getChild(Object parent, int index) {
        return (T) ((AbstractOMutableTreeNode) parent).getChildAt(index);
    }

    public void setRoot(T root) {
        if (this.root != null) {
            dispose();
        }
        /* listen to the leader treenode */
        root.addPropertyChangeListener(this);
        /* listen to the ontology model root */
        root.getUserObject().addPropertyChangeListener(this);
        /* */
        this.root = root;
        /* relayout */
        modelSupport.fireNewRoot();
    }

    @Override
    public int getChildCount(Object parent) {
        return ((AbstractOMutableTreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((AbstractOMutableTreeNode) parent).getIndex((TreeNode) child);
    }

    @Override
    public T getRoot() {
        return (T) super.getRoot();
    }

    public AbstractOMutableTreeNode modelToView(AbstractOMutableTreeNode from, Object userData) {
        AbstractOMutableTreeNode ret = null;
        if (from.getUserObject() != null && from.getUserObject().equals(userData)) {
            ret = from;
        } else if (ret == null) {
            for (int i = 0; i < from.getChildCount(); i++) {
                ret = modelToView((AbstractOMutableTreeNode) from.getChildAt(i), userData);
                if (ret != null) {
                    return ret;
                }
            }
        }
        return ret;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AbstractOMutableTreeNode.TREECHILD_INSERT)
                || evt.getPropertyName().equals(AbstractOMutableTreeNode.TREECHILD_WILLREMOVE)) {
            /* tree node structure changed */
            AbstractOMutableTreeNode parent = (AbstractOMutableTreeNode) evt.getSource();
            AbstractOMutableTreeNode child = (AbstractOMutableTreeNode) evt.getNewValue();
            boolean added = evt.getPropertyName().equals(AbstractOMutableTreeNode.TREECHILD_INSERT);
            /* update tree model state */
            if (parent != null && child != null) {
                treeChanged(parent, child, added);
            }
        }
        /* ontology model change */
        if (evt.getSource() instanceof OntologyNode) {
            /* find the parent root node */
            AbstractOMutableTreeNode parent = modelToView(getRoot(), evt.getSource());
            if (evt.getPropertyName().equals(OntologyNode.PROPERTY_NODE_ADDED)) {
                /* child wich was added to the parent */
                OntologyNode child = (OntologyNode) evt.getNewValue();
                /* find index in the parent list */
                int idx = parent.getUserObject().indexOf(child);
                /* and add it to the tree wrapper at the same position */
                if (idx != -1) {
                    parent.insert(TreeNodeFactory.def().createTreeNode(child), idx);
                } else {
                    parent.addNode(TreeNodeFactory.def().createTreeNode(child));
                }
                /* */
            } else if (evt.getPropertyName().equals(OntologyNode.PROPERTY_NODE_REMOVED)) {
                parent.remove(evt.getNewValue());
            } else {
                /* fire event for redraw/relayout node */
                modelSupport.firePathChanged(new TreePath(parent.getPath()));
            }
        }
    }

    public void treeChanged(AbstractOMutableTreeNode parent, AbstractOMutableTreeNode child, boolean added) {
        if (added) {
            modelSupport.fireChildAdded(new TreePath(parent.getPath()), parent.getIndex(child), child);
        } else {
            modelSupport.fireChildRemoved(new TreePath(parent.getPath()), parent.getIndex(child), child);
        }
    }

    public TreeModelSupport getModelSupport() {
        return modelSupport;
    }

    @Override
    public void dispose() {
        getRoot().getUserObject().removePropertyChangeListener(this);
        getRoot().removePropertyChangeListener(this);
        getRoot().removeFromParent();
    }
}
