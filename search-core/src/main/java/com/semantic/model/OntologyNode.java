/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OntologyNode extends SNode<OntologyNode, OntologyNode> {

    public static final String PROPERTY_NODE_NAME = "property_node_name";
    /* */
    @XmlAttribute(name = "name")
    protected String name;

    public OntologyNode() {
        this.name = "unknown";
    }

    public OntologyNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!this.name.equals(name)) {
            firePropertyChange(PROPERTY_NODE_NAME, this.name, this.name = name);
        }
    }
}