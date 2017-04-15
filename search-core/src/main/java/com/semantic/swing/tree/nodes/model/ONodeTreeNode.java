/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.nodes.model;

import com.l2fprod.common.propertysheet.Property;
import com.semantic.model.OntologyNode;
import com.semantic.swing.propertysheet.GenericProperty;
import com.semantic.swing.propertysheet.IPropertySheetNode;
import com.semantic.swing.tree.nodes.DefaultOntologyTreeNode;
import com.semantic.swing.tree.nodes.TreeNodeFactory;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <T>
 */
public class ONodeTreeNode<T extends OntologyNode> extends DefaultOntologyTreeNode<T>
        implements IPropertySheetNode {

    /* build in generic property support(only register your beaninfo) */
    protected Property property;

    public ONodeTreeNode(T node) {
        super(node);
        initNode();
    }

    protected void initNode() {
        int size = getUserObject().getNodeCount();
        for (int i = 0; i < size; i++) {
            addNode(TreeNodeFactory.def().createTreeNode(getUserObject().getChildAt(i)));
        }
    }

    @Override
    public Property[] createPropertys() {
        /* 
         * only take beaninfo's with explicit beaninfo classes:
         * BeanUtil.hasExplicitBeanInfo(getUserObject().getClass())
         */
        if (getUserObject() != null && property == null) {
            property = new GenericProperty(getUserObject());
        }
        /* return cached property */
        if (property != null) {
            return new Property[]{property};
        }
        return null;
    }
}