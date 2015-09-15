/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.undo;

import javax.swing.event.UndoableEditListener;

/**
 *
 * @author cplonka
 */
public interface UndoableModel {

    public void addUndoableEditListener(UndoableEditListener listener);

    public void removeUndoableEditListener(UndoableEditListener listener);
}