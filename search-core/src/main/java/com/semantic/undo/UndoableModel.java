/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.undo;

import javax.swing.event.UndoableEditListener;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface UndoableModel {

    public void addUndoableEditListener(UndoableEditListener listener);

    public void removeUndoableEditListener(UndoableEditListener listener);
}