/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.undo;

import com.semantic.model.OntologyNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class UndoableNodeModel implements UndoableModel, PropertyChangeListener {

    private UndoRedoProgress progress = new UndoRedoProgress();
    private transient EventListenerList listeners;
    private OntologyNode root;

    public void setModel(OntologyNode root) {
        if (this.root != null) {
            this.root.removePropertyChangeListener(this);
        }
        this.root = root;
        if (root != null) {
            root.addPropertyChangeListener(this);
        }
    }

    @Override
    public void addUndoableEditListener(UndoableEditListener listener) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(UndoableEditListener.class, listener);
    }

    @Override
    public void removeUndoableEditListener(UndoableEditListener listener) {
        if (listeners != null) {
            listeners.remove(UndoableEditListener.class, listener);
        }
    }

    protected void fireUndoableEditHappened(UndoableEdit edit) {
        if (listeners != null) {
            UndoableEditEvent evt = new UndoableEditEvent(this, edit);
            for (UndoableEditListener l : listeners.getListeners(UndoableEditListener.class)) {
                l.undoableEditHappened(evt);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        /* ontology model change */
        if ((!progress.isInProgress()) && evt.getSource() instanceof OntologyNode) {
            if (evt.getNewValue() instanceof OntologyNode) {
                /* child wich was added/removed to the parent */
                OntologyNode child = (OntologyNode) evt.getNewValue();
                if (evt.getPropertyName().equals(OntologyNode.PROPERTY_NODE_ADDED)) {
                    /* find index in the parent list */
                    int idx = child.getParent().indexOf(child);
                    fireUndoableEditHappened(new NodeUndoableEdit(progress, child,
                            (OntologyNode) evt.getSource(), idx, true));
                } else if (evt.getPropertyName().equals(OntologyNode.PROPERTY_NODE_REMOVED)) {
                    fireUndoableEditHappened(new NodeUndoableEdit(progress, child,
                            (OntologyNode) evt.getSource(), 0, false));
                }
            }
            /* */
            if (evt.getPropertyName().equals(OntologyNode.PROPERTY_NODE_NAME)) {
                fireUndoableEditHappened(new RenameNodeUndoableEdit(progress,
                        (OntologyNode) evt.getSource(), evt.getOldValue().toString(),
                        evt.getNewValue().toString()));
            }
        }
    }
}