/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OGroup extends OntologyNode {

    public OGroup() {
        this("Group");
    }

    public OGroup(String name) {
        super(name);
    }
}