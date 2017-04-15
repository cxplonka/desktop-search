/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.facet;

import static com.semantic.util.suggest.AutoCompleteListCellRenderer.*;
import com.semantic.util.suggest.Suggestion;
import com.semantic.util.swing.jlist.TopTermListCellRenderer;
import java.awt.Component;
import javax.swing.JList;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
class SuggestionListCellRenderer extends TopTermListCellRenderer {
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {        
        if (value instanceof Suggestion) {
            Suggestion suggest = (Suggestion) value;
            if (ICONS.containsKey(suggest.getField())) {                
                setIcon(ICONS.get(suggest.getField()));
            } else {
                setIcon(null);
            }
        }
        return super.getListCellRendererComponent(list, value, index, false, cellHasFocus);
    }
}
