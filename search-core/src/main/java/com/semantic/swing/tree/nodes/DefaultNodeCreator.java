/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes;

import com.semantic.model.OntologyNode;
import com.semantic.swing.tree.nodes.model.ONodeTreeNode;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <T>
 */
public class DefaultNodeCreator<T extends OntologyNode> implements ITreeNodeCreator<T> {

    protected static final Logger log = Logger.getLogger(DefaultNodeCreator.class.getName());
    private final Class<T> type;
    private final Class<AbstractOMutableTreeNode<T>> clazz;

    public DefaultNodeCreator(Class<T> type, Class<AbstractOMutableTreeNode<T>> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    @Override
    public AbstractOMutableTreeNode<T> createTreeNode(T node) {
        try {
            return clazz.getConstructor(type).newInstance(node);
        } catch (Exception ex) {
            /* can not create node, take default implementation */
            log.log(Level.WARNING, "problem while instance treenode!", ex);
        }
        return new ONodeTreeNode<T>(node);
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}