/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.beaninfo;

import com.semantic.model.filter.OMinMaxFilter;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.util.logging.Level;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OMinMaxFilterBeanInfo extends OntologyNodeBeanInfo {

    public OMinMaxFilterBeanInfo() {
        super();
        /* init beaninfo */
        try {
            beanDescriptor = new BeanDescriptor(OMinMaxFilter.class);
            beanDescriptor.setDisplayName("Filter - Size");

            PropertyDescriptor minSize = new PropertyDescriptor("minSize", OMinMaxFilter.class);
            minSize.setDisplayName("min:");
            
            PropertyDescriptor maxSize = new PropertyDescriptor("maxSize", OMinMaxFilter.class);
            maxSize.setDisplayName("max:");

            addPropertyDescriptor(minSize);
            addPropertyDescriptor(maxSize);
        } catch (Exception ex) {
            log.log(Level.FINER, null, ex);
        }
    }
}