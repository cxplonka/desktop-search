/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.propertysheet;

import com.semantic.swing.propertysheet.editor.MyNumberPropertyEditor;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.semantic.swing.propertysheet.editor.JXDatePickerPropertyEditor;
import java.util.Date;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OPropertyEditorRegistry extends PropertyEditorRegistry {

    private static OPropertyEditorRegistry ref;

    private OPropertyEditorRegistry() {
        registerEditor(Number.class, new MyNumberPropertyEditor());
        registerEditor(Date.class, new JXDatePickerPropertyEditor());
    }

    public synchronized static OPropertyEditorRegistry def() {
        if (ref == null) {
            ref = new OPropertyEditorRegistry();
        }
        return ref;
    }
}
