/*
 * GenericProperty.java
 *
 * Created on 10. Januar 2007, 14:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.semantic.swing.propertysheet;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class GenericProperty<T> extends DefaultProperty {

    protected static final Logger log = Logger.getLogger(GenericProperty.class.getName());
    /* */
    protected T userData;
    protected BeanDescriptor beanDesc;

    public GenericProperty(T userData) {
        this.userData = userData;
        /* inspect propertydescriptors in beaninfo */
        try {
            init(Introspector.getBeanInfo(userData.getClass()));
        } catch (IntrospectionException ex) {
            log.log(Level.FINER, "can not inspect bean!", ex);
        }
    }

    public GenericProperty(T userData, BeanInfo info) {
        this.userData = userData;
        init(info);
    }

    private void init(BeanInfo info) {
        if (userData != null) {
            beanDesc = info.getBeanDescriptor();

            setName(beanDesc.getName());
            setDisplayName(beanDesc.getDisplayName());
            setShortDescription(beanDesc.getShortDescription());

            PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            /* create properties for propertydescriptors */
            for (PropertyDescriptor p : propertyDescriptors) {
                SubProperty s = new SubProperty(p, this, userData);
                s.parentProperty = this;
                this.addSubProperty(s);
            }
        }
    }

    @Override
    public Class getType() {
        return userData != null ? userData.getClass() : Object.class;
    }

    @Override
    public Object getValue() {
        return userData;
    }

    @Override
    public String getCategory() {
        return getDisplayName();
    }

    public class SubProperty extends DefaultProperty {

        protected PropertyDescriptor desc;
        protected Property parentProperty = null;
        protected Object propOwner;

        public SubProperty(PropertyDescriptor desc, Property parentProperty,
                Object propOwner) {
            this.desc = desc;

            setEditable(!desc.isHidden());
            setName(desc.getName());
            setDisplayName(desc.getDisplayName());
            setShortDescription(desc.getShortDescription());

            this.propOwner = propOwner;
            this.parentProperty = parentProperty;
        }

        public PropertyDescriptor getPropertyDescriptor() {
            return desc;
        }

        @Override
        public Class getType() {
            return desc.getPropertyType();
        }

        @Override
        public Object getValue() {
            try {
                return desc.getReadMethod().invoke(propOwner, new Object[]{});
            } catch (Exception ex) {
                log.log(Level.FINER, "can not execute read method!", ex);
            }
            return null;
        }

        @Override
        public void setValue(Object value) {
            if ((value != null) && !getValue().equals(value)) {
                try {
                    desc.getWriteMethod().invoke(propOwner, new Object[]{value});
                    parentProperty.setValue(parentProperty.getValue());
                } catch (Exception ex) {
                    log.log(Level.FINER, "can not execute write method!", ex);
                }
            }
        }

        @Override
        public String getCategory() {
            return getName();
        }

        @Override
        public Property getParentProperty() {
            return parentProperty;
        }
    }
}