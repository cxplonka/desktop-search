/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.beaninfo;

import com.semantic.model.filter.OKeywordFilter;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.util.logging.Level;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OKeywordFilterBeanInfo extends OntologyNodeBeanInfo {

    public OKeywordFilterBeanInfo() {
        super();
        /* init beaninfo */
        try {
            beanDescriptor = new BeanDescriptor(OKeywordFilter.class);
            beanDescriptor.setDisplayName("Keyword Filter");

            PropertyDescriptor directory = new PropertyDescriptor("keyWord", OKeywordFilter.class);
            directory.setDisplayName("Keyword:");            

            addPropertyDescriptor(directory);
        } catch (Exception ex) {
            log.log(Level.FINER, null, ex);
        }
    }
}