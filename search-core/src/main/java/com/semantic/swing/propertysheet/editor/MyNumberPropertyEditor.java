/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.propertysheet.editor;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class MyNumberPropertyEditor extends AbstractPropertyEditor {

    public MyNumberPropertyEditor() {
        editor = new JTextField();        
        ((JTextField) editor).setBorder(LookAndFeelTweaks.EMPTY_BORDER);
    }

    @Override
    public Object getValue() {
        try {            
            Object r = Float.parseFloat(((JTextField)editor).getText());
            return r;
        } catch (Exception ex) {
            Logger.getLogger(MyNumberPropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public void setValue(Object arg0) {        
        ((JTextField) editor).setText(arg0.toString());
    }

    public static Object convert(Class type, Number value) {        
        if (value == null) {
            return 0;
        }

        if (Double.class.equals(type)) {
            return new Double(((Number) value).doubleValue());
        } else if (Float.class.equals(type)) {
            return new Float(((Number) value).floatValue());
        } else if (Integer.class.equals(type)) {
            return new Integer(((Number) value).intValue());
        } else if (Long.class.equals(type)) {
            return new Long(((Number) value).longValue());
        } else if (Short.class.equals(type)) {
            return new Short(((Number) value).shortValue());
        } else {
            throw new IllegalArgumentException("Number - Converter not supported...");
        }
    }
}