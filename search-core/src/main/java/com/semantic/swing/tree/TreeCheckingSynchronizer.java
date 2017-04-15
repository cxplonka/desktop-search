/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree;

import com.jidesoft.swing.CheckBoxTreeSelectionModel;
import com.semantic.model.OntologyNode;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TreeCheckingSynchronizer implements TreeSelectionListener, PropertyChangeListener {

    private OntologyNode root;

    public void setModel(OntologyNode root) {
        if (this.root != null) {
            this.root.removePropertyChangeListener(this);
        }
        this.root = root;
        if (this.root != null) {
            this.root.addPropertyChangeListener(this);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        CheckBoxTreeSelectionModel _smodel = (CheckBoxTreeSelectionModel) e.getSource();
        /* */
        for (TreePath path : e.getPaths()) {
            if (path.getLastPathComponent() instanceof AbstractOMutableTreeNode) {
                AbstractOMutableTreeNode node = (AbstractOMutableTreeNode) path.getLastPathComponent();
                /* full selected, dig in */
                node.setChecked(_smodel.isPathSelected(path, true));
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}