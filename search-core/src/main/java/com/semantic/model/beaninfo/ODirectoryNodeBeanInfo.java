/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.beaninfo;

import com.semantic.model.ODirectoryNode;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.util.logging.Level;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ODirectoryNodeBeanInfo extends OntologyNodeBeanInfo {

    public ODirectoryNodeBeanInfo() {
        super();
        /* init beaninfo */
        try {
            beanDescriptor = new BeanDescriptor(ODirectoryNode.class);
            beanDescriptor.setDisplayName("Index - Directory Node");

            PropertyDescriptor directory = new PropertyDescriptor("directory", ODirectoryNode.class, "directory", null);
            directory.setDisplayName("Directory:");
            directory.setHidden(true);

            addPropertyDescriptor(directory);
        } catch (Exception ex) {
            log.log(Level.FINER, null, ex);
        }
    }
}
