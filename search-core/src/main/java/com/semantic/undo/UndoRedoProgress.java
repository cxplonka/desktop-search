/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.undo;

/**
 *
 * @author cplonka
 */
public class UndoRedoProgress {

    private boolean progress = false;

    public void start() {
        progress = true;
    }

    public void stop() {
        progress = false;
    }

    public boolean isInProgress() {
        return progress;
    }
}