/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.undo;

import com.semantic.model.OntologyNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
class RenameNodeUndoableEdit extends AbstractUndoableEdit {

    private OntologyNode node;
    private String oldValue;
    private String newValue;
    private boolean switchName = true;
    private UndoRedoProgress progress;

    public RenameNodeUndoableEdit(UndoRedoProgress progress, OntologyNode node, String oldValue, String newValue) {
        this.node = node;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.progress = progress;
    }

    @Override
    public void undo() throws CannotUndoException {
        progress.start();
        try {
            super.undo();
            switchName();
        } finally {
            progress.stop();
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        progress.start();
        try {
            super.redo();
            switchName();
        } finally {
            progress.stop();
        }
    }

    private void switchName() {
        if (switchName) {
            node.setName(oldValue);
        } else {
            node.setName(newValue);
        }
        switchName = !switchName;
    }

    @Override
    public String getRedoPresentationName() {
        return String.format("Rename: [%s to %s]",
                switchName ? newValue : oldValue,
                switchName ? oldValue : newValue);
    }

    @Override
    public String getUndoPresentationName() {
        return getRedoPresentationName();
    }

    @Override
    public String getPresentationName() {
        return node.toString();
    }

    @Override
    public void die() {
        super.die();
        node = null;
    }
}