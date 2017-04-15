/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.beaninfo;

import com.semantic.model.OntologyNode;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.util.logging.Level;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OntologyNodeBeanInfo extends DefaultBeanInfo {

    public OntologyNodeBeanInfo() {
        super();
        /* init beaninfo */
        try {
            beanDescriptor = new BeanDescriptor(OntologyNode.class);
            beanDescriptor.setDisplayName("Ontology Node");

            PropertyDescriptor name = new PropertyDescriptor("name", OntologyNode.class);
            name.setDisplayName("Name:");

            addPropertyDescriptor(name);
        } catch (Exception ex) {
            log.log(Level.FINER, null, ex);
        }
    }
}
