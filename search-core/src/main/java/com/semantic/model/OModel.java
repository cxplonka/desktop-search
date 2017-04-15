/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
@XmlRootElement(name="root")
public class OModel extends OGroup {

    public OModel() {
        this("Scene");
    }

    public OModel(String name) {
        super(name);
    }
}