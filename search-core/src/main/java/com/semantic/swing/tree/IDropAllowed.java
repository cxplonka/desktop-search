/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree;

import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface IDropAllowed {

    public boolean isDropAllowed(AbstractOMutableTreeNode childNode);
}