/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.facet;

import com.semantic.ApplicationContext;
import com.semantic.eventbus.GenericEventListener;
import com.semantic.lucene.IndexManager;
import com.semantic.lucene.task.QueryResultEvent;
import com.semantic.lucene.util.IFieldProperty;
import com.semantic.plugin.PlugInManager;
import com.semantic.swing.tree.querybuilder.IQueryBuilder;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

/**
 *
 * @author cplonka
 */
public class FacetOverview extends javax.swing.JPanel implements GenericEventListener<QueryResultEvent>, IQueryBuilder {

    public FacetOverview() {
        initComponents();
    }

    @Override
    public Query createQuery() {
        BooleanQuery.Builder rootQuery = new BooleanQuery.Builder();

        ApplicationContext ctx = ApplicationContext.instance();
        IndexManager lucene = ctx.get(IndexManager.LUCENE_MANAGER);

        // drill down from base query
        DrillDownQuery dq = createDq(lucene.getFacetConfig());
        if (dq != null) {
            rootQuery.add(dq, Occur.FILTER);
        }

        return dq != null ? rootQuery.build() : null;
    }

    private DrillDownQuery createDq(FacetsConfig cfg) {
        DrillDownQuery dq = new DrillDownQuery(cfg);

        Map<IFieldProperty, List<String>> selectedValues = getSelectedLabels();
        for (Map.Entry<IFieldProperty, List<String>> entry : selectedValues.entrySet()) {
            for (String label : entry.getValue()) {
                dq.add(entry.getKey().getName(), label);
            }
        }

        return selectedValues.isEmpty() ? null : dq;
    }

    private Map<IFieldProperty, List<String>> getSelectedLabels() {
        Map<IFieldProperty, List<String>> ret = null;
        for (Component cmp : getComponents()) {
            if (cmp instanceof FacetFieldPanel) {
                FacetFieldPanel panel = (FacetFieldPanel) cmp;
                for (String label : panel.getSelectedLabels()) {
                    if (ret == null) {
                        ret = new HashMap<>();
                    }
                    List<String> labels = ret.get(panel.getDef());
                    if (labels == null) {
                        ret.put(panel.getDef(), labels = new ArrayList<>());
                    }
                    labels.add(label);
                }
            }
        }
        return ret == null ? Collections.emptyMap() : ret;
    }

    @Override
    public BooleanClause.Occur getCondition() {
        return Occur.MUST;
    }

    @Override
    public void handleEvent(QueryResultEvent event) {
        try {
            FacetsConfig cfg = event.getFacetConfig();

            Map<IFieldProperty, List<String>> labels = getSelectedLabels();
//            DrillDownQuery dq = createDq(cfg);
            // clean
            removeAll();

            PlugInManager pluginManager = ApplicationContext.instance().get(ApplicationContext.PLUGIN_MANAGER);

            IndexManager lucene = ApplicationContext.instance().get(IndexManager.LUCENE_MANAGER);
            IndexSearcher searcher = event.getCurrentSearcher();

            // facet collector
            FacetsCollector fc = new FacetsCollector();
            FacetsCollector.search(searcher, event.getQuery(), 5, fc);

//            DrillSideways.DrillSidewaysResult r = null;
//            if (dq != null) {
//                DrillSideways ds = new DrillSideways(searcher, cfg, lucene.getTaxoReader());
//                r = ds.search(dq, 5);
//            }
            for (IFieldProperty def : pluginManager.allInstances(IFieldProperty.class)) {
                if (def.hasFacet()) {
                    try {
                        String indexFieldName = cfg.getDimConfig(def.getName()).indexFieldName;

//                        if (r != null) {
//                            System.out.println(r.facets.getTopChildren(5, indexFieldName));
//                        }
                        Facets facets = new FastTaxonomyFacetCounts(
                                indexFieldName,
                                lucene.getTaxoReader(),
                                cfg,
                                fc);

                        FacetResult result = facets.getTopChildren(5, def.getName());
                        if (result != null && result.childCount > 0) {
                            FacetFieldPanel facetView = new FacetFieldPanel(def);
                            facetView.setResult(result,
                                    labels.containsKey(def) ? labels.get(def) : Collections.emptyList());
                            add(facetView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            repaint();
        } catch (IOException ex) {
            Logger.getLogger(FacetOverview.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
