/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.propertysheet.editor;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import javax.swing.JComboBox;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class EnumComboBoxPropertyEditor extends ComboBoxPropertyEditor {

    @Override
    public void setValue(final Object value) {
        final JComboBox box = (JComboBox) editor;

        box.removeAllItems();
        if (box.getItemCount() == 0) {
            try {
                final java.lang.reflect.Method m = value.getClass().getMethod(
                        "values");
                final Enum<?>[] array = (Enum[]) m.invoke(null);
                setAvailableValues(array);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        super.setValue(value);
    }

}
