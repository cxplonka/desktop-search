/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.beaninfo;

import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DefaultBeanInfo extends SimpleBeanInfo {

    protected static final Logger log = Logger.getLogger(DefaultBeanInfo.class.getName());
    /* */
    protected BeanDescriptor beanDescriptor;
    protected List<PropertyDescriptor> propertyDescriptors;

    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return propertyDescriptors == null ? null : propertyDescriptors.toArray(
                new PropertyDescriptor[propertyDescriptors.size()]);
    }

    @Override
    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptor;
    }
    
    public void addPropertyDescriptor(PropertyDescriptor descriptor){
        if(propertyDescriptors == null){
            propertyDescriptors = new ArrayList<PropertyDescriptor>();
        }
        propertyDescriptors.add(descriptor);
    }
    
    public void removePropertyDescriptor(PropertyDescriptor descriptor){
        if(propertyDescriptors != null){
            propertyDescriptors.remove(descriptor);
        }
    }
}