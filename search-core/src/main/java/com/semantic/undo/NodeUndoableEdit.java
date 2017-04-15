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
class NodeUndoableEdit extends AbstractUndoableEdit {

    private OntologyNode node;
    private OntologyNode root;
    private boolean insert;
    private int idx;
    private UndoRedoProgress progress;

    public NodeUndoableEdit(UndoRedoProgress progress, OntologyNode node, OntologyNode root, int idx, boolean insert) {
        this.node = node;
        this.root = root;
        this.insert = insert;
        this.idx = idx;
        this.progress = progress;
    }

    @Override
    public void undo() throws CannotUndoException {
        progress.start();
        try {
            super.undo();
            if (insert) {
                root.removeNode(node);
            } else {
                root.insertNode(idx, node);
            }
        } finally {
            progress.stop();
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        progress.start();
        try {
            super.redo();
            if (!insert) {
                root.removeNode(node);
            } else {
                root.insertNode(idx, node);
            }
        } finally {
            progress.stop();
        }
    }

    @Override
    public String getRedoPresentationName() {
        if (!insert) {
            return String.format("Remove: [%s from %s]", node.getName(), root.getName());
        } else {
            return String.format("Insert: [%s into %s]", node.getName(), root.getName());
        }
    }

    @Override
    public String getUndoPresentationName() {
        if (insert) {
            return String.format("Remove: [%s from %s]", node.getName(), root.getName());
        } else {
            return String.format("Insert: [%s into %s]", node.getName(), root.getName());
        }
    }

    @Override
    public String getPresentationName() {
        return node.toString();
    }

    @Override
    public void die() {
        super.die();
        node = null;
        root = null;
    }
}