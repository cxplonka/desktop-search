/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes;

import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import com.semantic.swing.tree.IActionNode;
import com.semantic.util.property.IPropertyKey;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <T>
 */
public class DefaultOntologyTreeNode<T extends OntologyNode> extends AbstractOMutableTreeNode<T> implements IActionNode {

    protected Action removeAction;
    protected Action orAction;
    protected Action notAction;

    public DefaultOntologyTreeNode(T userData) {
        super(userData);
    }

    @Override
    public List<MutableTreeNode> createLazyChildList() {
        return new ArrayList<MutableTreeNode>();
    }

    @Override
    public String getDisplayName() {
        return getUserObject().getName();
    }

    @Override
    public Action[] getActions() {
        if (removeAction == null) {
            removeAction = new RemoveAction();
        }
        /* boolean clause actions */
        if (orAction == null || notAction == null) {
            orAction = new OrAction();
            notAction = new NotAction();
        }
        /* */
        orAction.putValue(Action.NAME, String.format("With \"%s\"",
                getUserObject().getName()));
        notAction.putValue(Action.NAME, String.format("Without \"%s\"",
                getUserObject().getName()));
        /* null action means, we want and seperator there */
        return new Action[]{orAction, notAction, null, removeAction};
    }

    class RemoveAction extends AbstractAction {

        public RemoveAction() {
            super("Remove Node");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            /* remove node from ontology model */
            getUserObject().removeFromParent();
        }
    }

    private static <T> void applyProperty(OntologyNode node, IPropertyKey<T> key, T value) {
        /* only modify children nodes */
        for (int i = 0; i < node.getNodeCount(); i++) {
            node.getChildAt(i).set(key, value);
            applyProperty(node.getChildAt(i), key, value);
        }        
    }

    class OrAction extends AbstractAction {

        public OrAction() {
            super("With");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            /* turn off forwarding property if no leaf node */
            if (getUserObject().getNodeCount() > 0) {
                getUserObject().setForwardPropertyChange(false);
                /* set for childrens */
                applyProperty(getUserObject(), IQueryGenerator.BOOLEAN_CLAUSE,
                        IQueryGenerator.CLAUSE.OR);
                /* turn on and set state */
                getUserObject().setForwardPropertyChange(true);
            }
            /* set own state */
            getUserObject().set(IQueryGenerator.BOOLEAN_CLAUSE,
                    IQueryGenerator.CLAUSE.OR);
        }
    }

    class NotAction extends AbstractAction {

        public NotAction() {
            super("Without");
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            /* turn off forwarding property if no leaf node */
            if (getUserObject().getNodeCount() > 0) {
                getUserObject().setForwardPropertyChange(false);
                /* set for childrens */
                applyProperty(getUserObject(), IQueryGenerator.BOOLEAN_CLAUSE,
                        IQueryGenerator.CLAUSE.NOT);
                /* turn on and set state */
                getUserObject().setForwardPropertyChange(true);
            }
            /* set own state */
            getUserObject().set(IQueryGenerator.BOOLEAN_CLAUSE,
                    IQueryGenerator.CLAUSE.NOT);
        }
    }
}