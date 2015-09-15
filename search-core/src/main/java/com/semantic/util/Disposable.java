/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.semantic.util;

/**
 * Should be implement by Object's who will be disposed.
 * @author cplonka
 */
public interface Disposable {

    /**
     * Invoked when the Object is no longer needed.
     */
    public void dispose();
}
