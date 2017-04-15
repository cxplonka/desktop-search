/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jtree;

import com.semantic.util.swing.jtree.TreeModelSupport;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 ** from swingx
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public abstract class AbstractTreeTableModel implements TreeModel {

    /**
     * Root node of the model
     */
    protected Object root;

    /**
     * Provides support for event dispatching.
     */
    protected TreeModelSupport modelSupport;
    
    /**
     * Constructs an {@code AbstractTableModel} with a {@code null} root
     * node.
     */
    public AbstractTreeTableModel() {
        this(null);
    }

    /**
     * Constructs an {@code AbstractTableModel} with the specified root
     * node.
     * 
     * @param root
     *            root node
     */
    public AbstractTreeTableModel(Object root) {
        this.root = root;
        this.modelSupport = new TreeModelSupport(this);
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getColumnClass(int column) {
        return Object.class;
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnName(int column) {
        //Copied from AbstractTableModel.
        //Should use same defaults when possible.
        String result = "";
        
        for (; column >= 0; column = column / 26 - 1) {
            result = (char) ((char) (column % 26) + 'A') + result;
        }
        
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Object getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(Object node, int column) {
        // RG: Fix Issue 49 -- Cell not editable, by default.
        // Subclasses might override this to return true.
        return false;
    }

    /**
     * Returns <code>true</code> if <code>node</code> is a leaf.
     *
     * @impl {@code true} if {@code getChildCount(node) == 0}
     * @param   node  a node in the tree, obtained from this data source
     * @return  true if <code>node</code> is a leaf
     */
    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    /**
     * Sets the value for the {@code node} at {@code columnIndex} to
     * {@code value}.
     * 
     * @impl is no-op; provided for convenience for uneditable models
     * @param value
     *            the new value
     * @param node
     *            the node whose value is to be changed
     * @param column
     *            the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object value, Object node, int column) {
        //does nothing
    }
    
    /**
     * Called when value for the item identified by path has been changed. If
     * newValue signifies a truly new value the model should post a
     * {@code treeNodesChanged} event.
     * <p>
     * 
     * @impl is no-op. A {@code JXTreeTable} does not usually edit the node directly.
     * @param path
     *            path to the node that has changed
     * @param newValue
     *            the new value from the <code>TreeCellEditor</code>
     */
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        //does nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTreeModelListener(TreeModelListener l) {
        modelSupport.addTreeModelListener(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        modelSupport.removeTreeModelListener(l);
    }

    /**
     * Returns an array of all the <code>TreeModelListener</code>s added
     * to this JXTreeTable with addTreeModelListener().
     *
     * @return all of the <code>TreeModelListener</code>s added or an empty
     *         array if no listeners have been added
     */
    public TreeModelListener[] getTreeModelListeners() {
        return modelSupport.getTreeModelListeners();
    }
}