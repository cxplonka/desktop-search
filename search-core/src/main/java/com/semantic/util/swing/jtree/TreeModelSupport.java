/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jtree;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * from swingx
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class TreeModelSupport {

    protected EventListenerList listeners;
    private TreeModel treeModel;

    public TreeModelSupport(TreeModel model) {
        if (model == null) {
            throw new NullPointerException("model must not be null");
        }
        listeners = new EventListenerList();
        this.treeModel = model;
    }

    public void fireNewRoot() {
        Object root = treeModel.getRoot();
        fireTreeStructureChanged((root != null) ? new TreePath(root) : null);
    }

    public void firePathLeafStateChanged(TreePath path) {
        fireTreeStructureChanged(path);
    }

    public void fireTreeStructureChanged(TreePath subTreePath) {
        Object[] pairs = listeners.getListenerList();
        TreeModelEvent e = null;
        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null) {
                    e = createStructureChangedEvent(subTreePath);
                }
                ((TreeModelListener) pairs[i + 1]).treeStructureChanged(e);
            }
        }
    }

    public void firePathChanged(TreePath path) {
        Object node = path.getLastPathComponent();
        TreePath parentPath = path.getParentPath();

        if (parentPath == null) {
            fireChildrenChanged(path, null, null);
        } else {
            fireChildChanged(parentPath, treeModel.getIndexOfChild(
                    parentPath.getLastPathComponent(), node), node);
        }
    }

    public void fireChildChanged(TreePath parentPath, int index, Object child) {
        fireChildrenChanged(parentPath, new int[]{index},
                new Object[]{child});
    }

    public void fireChildrenChanged(TreePath parentPath, int[] indices,
            Object[] children) {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null) {
                    e = createTreeModelEvent(parentPath, indices, children);
                }
                ((TreeModelListener) pairs[i + 1]).treeNodesChanged(e);
            }
        }
    }

    public void fireChildAdded(TreePath parentPath, int index, Object child) {
        fireChildrenAdded(parentPath, new int[]{index},
                new Object[]{child});
    }

    public void fireChildRemoved(TreePath parentPath, int index, Object child) {
        fireChildrenRemoved(parentPath, new int[]{index},
                new Object[]{child});
    }

    public void fireChildrenAdded(TreePath parentPath, int[] indices,
            Object[] children) {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null) {
                    e = createTreeModelEvent(parentPath, indices, children);
                }
                ((TreeModelListener) pairs[i + 1]).treeNodesInserted(e);
            }
        }
    }

    public void fireChildrenRemoved(TreePath parentPath, int[] indices,
            Object[] children) {
        Object[] pairs = listeners.getListenerList();

        TreeModelEvent e = null;

        for (int i = pairs.length - 2; i >= 0; i -= 2) {
            if (pairs[i] == TreeModelListener.class) {
                if (e == null) {
                    e = createTreeModelEvent(parentPath, indices, children);
                }
                ((TreeModelListener) pairs[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    private TreeModelEvent createStructureChangedEvent(TreePath parentPath) {
        return createTreeModelEvent(parentPath, null, null);
    }

    private TreeModelEvent createTreeModelEvent(TreePath parentPath,
            int[] indices, Object[] children) {
        return new TreeModelEvent(treeModel, parentPath, indices, children);
    }

    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(TreeModelListener.class, l);
    }

    public TreeModelListener[] getTreeModelListeners() {
        return listeners.getListeners(TreeModelListener.class);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(TreeModelListener.class, l);
    }
}