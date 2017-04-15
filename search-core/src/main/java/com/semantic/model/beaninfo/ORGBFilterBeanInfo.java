/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.beaninfo;

import com.semantic.model.filter.ORGBFilter;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.util.logging.Level;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ORGBFilterBeanInfo extends OntologyNodeBeanInfo {

    public ORGBFilterBeanInfo() {
        super();
        /* init beaninfo */
        try {
            beanDescriptor = new BeanDescriptor(ORGBFilter.class);
            beanDescriptor.setDisplayName("RGB Mean Filter");

            PropertyDescriptor rangeRGB = new PropertyDescriptor("rangeRGB", ORGBFilter.class);
            rangeRGB.setDisplayName("Range RGB:");

            addPropertyDescriptor(rangeRGB);
        } catch (Exception ex) {
            log.log(Level.FINER, null, ex);
        }
    }
}
