/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.beaninfo;

import com.semantic.model.filter.OFileDateFilter;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.util.logging.Level;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OFileDateFilterBeanInfo extends OntologyNodeBeanInfo {

    public OFileDateFilterBeanInfo() {
        super();
        /* init beaninfo */
        try {
            beanDescriptor = new BeanDescriptor(OFileDateFilter.class);
            beanDescriptor.setDisplayName("Filter - Date");

            PropertyDescriptor fromDate = new PropertyDescriptor("fromDate", OFileDateFilter.class);
            fromDate.setDisplayName("From:");
            
            PropertyDescriptor toDate = new PropertyDescriptor("toDate", OFileDateFilter.class);
            toDate.setDisplayName("To:");
            
            addPropertyDescriptor(fromDate);
            addPropertyDescriptor(toDate);
        } catch (Exception ex) {
            log.log(Level.FINER, null, ex);
        }
    }
}