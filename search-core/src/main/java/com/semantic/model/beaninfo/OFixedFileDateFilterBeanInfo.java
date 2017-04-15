/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.beaninfo;

import com.semantic.model.filter.OFixedFileDateFilter;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.util.logging.Level;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OFixedFileDateFilterBeanInfo extends OntologyNodeBeanInfo {

    public OFixedFileDateFilterBeanInfo() {
        super();
        /* init beaninfo */
        try {
            beanDescriptor = new BeanDescriptor(OFixedFileDateFilter.class);
            beanDescriptor.setDisplayName("Filter - Date");

            PropertyDescriptor date = new PropertyDescriptor("date", OFixedFileDateFilter.class);
            date.setDisplayName("Select:");

            if (propertyDescriptors != null) {
                propertyDescriptors.clear();
            }

            addPropertyDescriptor(date);
        } catch (Exception ex) {
            log.log(Level.FINER, null, ex);
        }
    }
}
