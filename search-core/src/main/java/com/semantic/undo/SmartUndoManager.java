/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.undo;

import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SmartUndoManager extends DefaultUndoManager implements Runnable {

    private UndoableEdit edit;

    @Override
    public void undoableEditHappened(UndoableEditEvent event) {
        if (edit == null) {
            edit = event.getEdit();
            SwingUtilities.invokeLater(this);
        } else if (!edit.addEdit(event.getEdit())) {
            /* compound events */
            CompoundEdit compoundEdit = new CompoundEdit();
            compoundEdit.addEdit(edit);
            compoundEdit.addEdit(event.getEdit());
            edit = compoundEdit;
        }
    }

    @Override
    public void run() {
        if (edit instanceof CompoundEdit) {
            CompoundEdit compoundEdit = (CompoundEdit) edit;
            if (compoundEdit.isInProgress()) {
                compoundEdit.end();
            }
        }
        super.undoableEditHappened(new UndoableEditEvent(this, edit));
        edit = null;
    }
}