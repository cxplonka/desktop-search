/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.semantic.util;

/**
 *
 * @param <T> 
 * @author cplonka
 */
public interface VisitorPattern<T> {
    /**
     *
     * @param node
     * @return if found node - false(parsing stops)
     */
    public boolean visit(T node);
}
