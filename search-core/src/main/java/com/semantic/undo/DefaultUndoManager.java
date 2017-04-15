/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.undo;

import com.semantic.util.image.TextureManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DefaultUndoManager extends UndoManager {

    private Action undoAction = new UndoAction();
    private Action redoAction = new RedoAction();

    public DefaultUndoManager() {
        super();
        stateChanged();
    }

    public Action getUndoAction() {
        return undoAction;
    }

    public Action getRedoAction() {
        return redoAction;
    }

    @Override
    public synchronized void undo() throws CannotUndoException {
        super.undo();
        stateChanged();
    }

    @Override
    public synchronized void redo() throws CannotRedoException {
        super.redo();
        stateChanged();
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
        super.undoableEditHappened(undoableEditEvent);
        stateChanged();
    }

    private void stateChanged() {
        undoAction.setEnabled(canUndo());
        redoAction.setEnabled(canRedo());
        undoAction.putValue(Action.SHORT_DESCRIPTION, getUndoPresentationName());
        redoAction.putValue(Action.SHORT_DESCRIPTION, getRedoPresentationName());
    }

    private class UndoAction extends AbstractAction {

        public UndoAction() {
            super("Undo");
            putValue(SHORT_DESCRIPTION, "Undo");
            putValue(SMALL_ICON, new ImageIcon(TextureManager.def().loadImage("arrow_back.png")));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            undo();
        }
    }

    private class RedoAction extends AbstractAction {

        public RedoAction() {
            super("Redo");
            putValue(SHORT_DESCRIPTION, "Redo");
            putValue(SMALL_ICON, new ImageIcon(TextureManager.def().loadImage("arrow_front.png")));
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            redo();
        }
    }
}