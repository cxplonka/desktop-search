/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model;

import com.semantic.util.property.PropertyMap;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 * @param <Parent>
 * @param <Child>
 */
@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.NONE)
public class SNode<Parent extends SNode, Child extends SNode> extends PropertyMap {

    public static final String PROPERTY_NODE_ADDED = "property_node_added";
    public static final String PROPERTY_NODE_REMOVED = "property_node_removed";
    /* like to propagate up to the root */
    private boolean forwardPropertyChange = true;
    /* our parent */
    protected Parent parent;
    @XmlElement(name = "node")
    private List<Child> childs;

    protected List<Child> createLazyChildList() {
        if (childs == null) {
            childs = new ArrayList<Child>();
        }
        return childs;
    }

    protected List<Child> getChildReferenceList() {
        return childs;
    }

    public void setForwardPropertyChange(boolean forwardPropertyChange) {
        this.forwardPropertyChange = forwardPropertyChange;
    }

    public boolean isForwardPropertyChange() {
        return forwardPropertyChange;
    }

    public void addNode(Child node) {
        if (getChildReferenceList() == null) {
            createLazyChildList();
        }
        /* set the new parent */
        node.setParent(this);
        /* add to our childlist */
        getChildReferenceList().add(node);
        firePropertyChange(PROPERTY_NODE_ADDED, null, node);
    }

    public void insertNode(int idx, Child node) {
        if (getChildReferenceList() == null) {
            createLazyChildList();
        }
        /* set the new parent */
        node.setParent(this);
        /* add to our childlist */
        getChildReferenceList().add(idx, node);
        firePropertyChange(PROPERTY_NODE_ADDED, null, node);
    }

    public int indexOf(Child child) {
        if (getChildReferenceList() != null) {
            return getChildReferenceList().indexOf(child);
        }
        return -1;
    }

    public Child removeNode(int idx) {
        if (childs != null) {
            Child ret = childs.remove(idx);
            ret.setParent(null);
            firePropertyChange(PROPERTY_NODE_REMOVED, null, ret);
            return ret;
        }
        return null;
    }

    public Child removeNode(Child node) {
        Child ret = null;
        /* search for the child */
        if (getChildReferenceList() != null) {
            int idx = getChildReferenceList().indexOf(node);
            if (idx != -1) {
                ret = removeNode(idx);
            }
        }
        return ret;
    }

    public Child getChildAt(int idx) {
        if (getChildReferenceList() != null) {
            return getChildReferenceList().get(idx);
        }
        return null;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Parent getParent() {
        return parent;
    }

    public void removeFromParent() {
        if (parent != null) {
            parent.removeNode(this);
        }
    }

    public int getNodeCount() {
        if (getChildReferenceList() == null) {
            return 0;
        }
        return getChildReferenceList().size();
    }

    public SNode getRoot() {
        if (parent != null) {
            return parent.getRoot();
        }
        return this;
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent evt = null;
        if (propertyChangeSupport != null) {
            super.firePropertyChange(evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue));
        }
        /* fire up to parent */
        if (parent != null && forwardPropertyChange) {
            parent.firePropertyChange(evt != null ? evt : new PropertyChangeEvent(this, propertyName, oldValue, newValue));
        }
    }

    @Override
    public void firePropertyChange(PropertyChangeEvent evt) {
        if (propertyChangeSupport != null) {
            super.firePropertyChange(evt);
        }
        /* fire up to parent */
        if (parent != null && forwardPropertyChange) {
            parent.firePropertyChange(evt);
        }
    }
}