/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util;

import java.beans.Introspector;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class BeanUtil {

    public static boolean hasExplicitBeanInfo(Class clazz) {
        String className = clazz.getName();
        int indx = className.lastIndexOf('.');
        className = className.substring(indx + 1);

        String[] paths = Introspector.getBeanInfoSearchPath();
        for (String path : paths) {
            String s = path + '.' + className + "BeanInfo"; // NOI18N
            try {
                // test if such class exists
                Class.forName(s);
                return true;
            } catch (ClassNotFoundException ex) {
                // OK, this is normal.
            }
        }
        return false;
    }
}