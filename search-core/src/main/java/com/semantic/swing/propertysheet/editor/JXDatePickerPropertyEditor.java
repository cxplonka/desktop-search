/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.propertysheet.editor;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.util.Date;
import java.util.Locale;
import org.jdesktop.swingx.JXDatePicker;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class JXDatePickerPropertyEditor extends AbstractPropertyEditor {

    public JXDatePickerPropertyEditor() {
        editor = new JXDatePicker();
    }

    @Override
    public Object getValue() {
        return ((JXDatePicker) editor).getDate();
    }

    @Override
    public void setValue(Object value) {
        if (value != null) {
            ((JXDatePicker) editor).setDate((Date) value);
        }
    }

    @Override
    public String getAsText() {
        Date date = (Date) getValue();
//        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
//                getDateFormatString());
//        String s = formatter.format(date);
        return date.toString();
    }
}