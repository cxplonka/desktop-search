/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.facet;

import com.semantic.util.image.TextureManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import org.apache.lucene.facet.Facets;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FacetJTabbedPane extends JTabbedPane {

    private final Map<String, FacetFieldPanel> faceted = new HashMap<String, FacetFieldPanel>();

    public FacetJTabbedPane() {
        initComponents();
    }

    private void initComponents() {
        faceted.put("Date", new FacetFieldPanel("Date"));
        faceted.put("Author", new FacetFieldPanel("Author"));

        addTab(null, new ImageIcon(TextureManager.def().loadImage(
                "16x16/date_icon.png")), faceted.get("Date"));
        addTab(null, new ImageIcon(TextureManager.def().loadImage(
                "16x16/author_icon.png")), faceted.get("Author"));
    }

    public void handleResult(Facets facets) {
        try {
            for (String key : faceted.keySet()) {
                faceted.get(key).setResult(facets.getTopChildren(5, key));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
