/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes;

import com.semantic.model.OntologyNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface ITreeNodeCreator<T extends OntologyNode> {

    public AbstractOMutableTreeNode<T> createTreeNode(T node);

    public Class<T> getType();
}
