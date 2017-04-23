/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.facet;

import com.semantic.ApplicationContext;
import com.semantic.lucene.IndexManager;
import com.semantic.lucene.task.QueryResultEvent;
import com.semantic.lucene.util.IFieldProperty;
import com.semantic.plugin.PlugInManager;
import com.semantic.util.image.TextureManager;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.search.IndexSearcher;

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
        PlugInManager pluginManager = ApplicationContext.instance().get(ApplicationContext.PLUGIN_MANAGER);
        for (IFieldProperty def : pluginManager.allInstances(IFieldProperty.class)) {
            if (def.hasFacet()) {
                faceted.put(def.getName(), new FacetFieldPanel(def));
                addTab(null, new ImageIcon(TextureManager.def().loadImage(
                        "16x16/date_icon.png")), faceted.get(def.getName()));
            }
        }
    }

    public void handleResult(QueryResultEvent event) throws IOException {
        PlugInManager pluginManager = ApplicationContext.instance().get(ApplicationContext.PLUGIN_MANAGER);

        IndexManager lucene = ApplicationContext.instance().get(IndexManager.LUCENE_MANAGER);
        IndexSearcher searcher = event.getCurrentSearcher();

        /* facet collector */
        FacetsCollector fc = new FacetsCollector();
        FacetsConfig cfg = event.getFacetConfig();
        FacetsCollector.search(searcher, event.getQuery(), 5, fc);

        for (IFieldProperty def : pluginManager.allInstances(IFieldProperty.class)) {
            if (def.hasFacet()) {
                try {
                    String indexFieldName = cfg.getDimConfig(def.getName()).indexFieldName;

                    Facets facets = new FastTaxonomyFacetCounts(
                            indexFieldName,
                            lucene.getTaxoReader(),
                            cfg,
                            fc);

                    faceted.get(def.getName()).setResult(facets.getTopChildren(5, def.getName()), Collections.emptyList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
