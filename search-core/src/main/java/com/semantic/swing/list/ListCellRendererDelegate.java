/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.list;

import com.semantic.ApplicationContext;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.plugin.PlugInManager;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ListCellRendererDelegate implements ListCellRenderer {

    private final DefaultListDocumentRenderer defaultRender = new DefaultListDocumentRenderer();
    private final Map<String, MimeTypeListCellRenderer> registry
            = new HashMap<String, MimeTypeListCellRenderer>();

    public ListCellRendererDelegate() {
        /* load delegates */
        ApplicationContext ctx = ApplicationContext.instance();
        PlugInManager plug = ctx.get(ApplicationContext.PLUGIN_MANAGER);
        for (MimeTypeListCellRenderer renderer : plug.allInstances(MimeTypeListCellRenderer.class)) {
            registry.put(renderer.getType(), renderer);
        }
    }

    @Override
    public Component getListCellRendererComponent(JList jlist, Object e, int i, boolean bln, boolean bln1) {
        Component ret = null;
        if (e instanceof Document) {
            Document doc = (Document) e;
            String type = doc.get(MimeTypeField.NAME);
            if (type != null && registry.containsKey(type)) {
                ret = registry.get(type).getListCellRendererComponent(jlist, doc, i, bln, bln1);
            } else {
                ret = defaultRender.getListCellRendererComponent(jlist, doc, i, bln, bln1);
            }
        }
        return ret == null ? defaultRender.getListCellRendererComponent(jlist, null, i, bln, bln1) : ret;
    }
}
