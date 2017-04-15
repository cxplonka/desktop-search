/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import com.semantic.lucene.fields.image.ExifMakeField;
import com.semantic.lucene.fields.image.ExifModelField;
import com.semantic.lucene.util.IFieldProperty;
import com.semantic.util.image.TextureManager;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class AutoCompleteListCellRenderer extends DefaultListCellRenderer {

    public static final Map<String, Icon> ICONS = new HashMap<String, Icon>();

    static {        
        ICONS.put("dc:creator" + IFieldProperty.EXT_SUGGEST, new ImageIcon(TextureManager.def().loadImage("16x16/contact.png")));
        ICONS.put("dc:title" + IFieldProperty.EXT_SUGGEST, new ImageIcon(TextureManager.def().loadImage("16x16/book.png")));
        ICONS.put(ExifModelField.NAME + IFieldProperty.EXT_SUGGEST, new ImageIcon(TextureManager.def().loadImage("16x16/model.png")));
        ICONS.put(ExifMakeField.NAME + IFieldProperty.EXT_SUGGEST, new ImageIcon(TextureManager.def().loadImage("16x16/make.png")));
    }

    @Override
    public Component getListCellRendererComponent(JList jlist, Object e, int i, boolean bln, boolean bln1) {
        Component ret = super.getListCellRendererComponent(jlist, e, i, bln, bln1);
        if (e instanceof Suggestion) {
            Suggestion suggest = (Suggestion) e;
            if (ICONS.containsKey(suggest.getField())) {
                setIcon(ICONS.get(suggest.getField()));
            }
        }
        return ret;
    }
}