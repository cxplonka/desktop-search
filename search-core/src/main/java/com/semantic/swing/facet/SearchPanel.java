/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.facet;

import com.jidesoft.popup.JidePopup;
import com.semantic.lucene.fields.ContentField;
import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.lucene.fields.SizeField;
import com.semantic.lucene.task.LuceneQueryTask;
import com.semantic.lucene.task.QueryResultEvent;
import com.semantic.swing.ShapedTransculentPopup;
import com.semantic.swing.UIDefaults;
import com.semantic.swing.tree.querybuilder.IQueryBuilder;
import com.semantic.swing.tree.querybuilder.QueryRefreshAction;
import com.semantic.util.image.TextureManager;
import com.semantic.util.suggest.NewSuggestionHints;
import com.semantic.util.suggest.Suggestion;
import com.semantic.util.swing.jlist.RemoveableListManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.suggest.Lookup;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.combobox.MapComboBoxModel;
import org.jdesktop.swingx.prompt.BuddySupport;
import org.jdesktop.swingx.prompt.PromptSupport;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SearchPanel extends javax.swing.JPanel implements IQueryBuilder {

    private static final Map<String, Sort> SORT_BY = new HashMap<String, Sort>();
    private final Action refreshAction = new QueryRefreshAction();
    private NewSuggestionHints hints;
    private DefaultListModel<Suggestion> listModel;
    private final FacetJTabbedPane facetPane = new FacetJTabbedPane();

    static {
        SORT_BY.put("last modified", new Sort(new SortField(LastModifiedField.NAME,
                SortField.Type.LONG, true)));
        SORT_BY.put("file size", new Sort(new SortField(SizeField.NAME,
                SortField.Type.LONG, true)));
        SORT_BY.put("relevance", Sort.RELEVANCE);
        SORT_BY.put("indexorder", Sort.INDEXORDER);
    }

    public SearchPanel() {
        initComponents();
        initOwnComponents();
    }

    private void initOwnComponents() {
        jtfSearch.setBorder(UIManager.getBorder(UIDefaults.BORDER_GRID_VIEW));
        jComboBox1.setModel(new MapComboBoxModel(SORT_BY));
        for (Map.Entry<String, Sort> entry : SORT_BY.entrySet()) {
            if (entry.getValue().equals(LuceneQueryTask.SORT)) {
                jComboBox1.setSelectedItem(entry.getKey());
            }
        }
        jComboBox1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Sort selection = SORT_BY.get(jComboBox1.getSelectedItem().toString());
                if (!LuceneQueryTask.SORT.equals(selection)) {
                    LuceneQueryTask.SORT = selection;
                }
                refreshAction.actionPerformed(e);
            }
        });
        /* search field */
        jtfSearch.setSearchMode(JXSearchField.SearchMode.REGULAR);
        jtfSearch.setAction(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String text = jtfSearch.getText();
                if (!text.isEmpty()) {
                    listModel.addElement(Suggestion.create(
                            new Lookup.LookupResult(text, 0), null, null));
                }
            }
        });

        PromptSupport.setPrompt("Search...", jtfSearch);

//        JButton button = ComponentFactory.Helper.getFactory().createMiniButton();
//        button.setAction(new FacetAction());
//        BuddySupport.addRight(button, jtfSearch);
        JList list = new JList(listModel = new DefaultListModel());
        listModel.addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent lde) {
                jtfSearch.setText("");
                refreshAction.actionPerformed(null);
            }

            @Override
            public void intervalRemoved(ListDataEvent lde) {
                refreshAction.actionPerformed(null);
            }

            @Override
            public void contentsChanged(ListDataEvent lde) {
            }
        });
        hints = new NewSuggestionHints(jtfSearch, listModel);
        list.setOpaque(false);
        /* icons */
        list.setCellRenderer(new SuggestionListCellRenderer());
        list.setFixedCellWidth(100);
        list.setVisibleRowCount(1);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        BuddySupport.addLeft(list, jtfSearch);
        RemoveableListManager removeableListManager = new RemoveableListManager(list);
    }

    public void handleEvent(QueryResultEvent event) {
        hints.setIndexReader(event.getCurrentSearcher().getIndexReader());
        handleFacet(event);
    }

    private void handleFacet(QueryResultEvent event) {
        try {
            facetPane.handleResult(event);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jtfSearch = new org.jdesktop.swingx.JXSearchField();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jtfSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jtfSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jtfSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtfSearchActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private org.jdesktop.swingx.JXSearchField jtfSearch;
    // End of variables declaration//GEN-END:variables

    @Override
    public Query createQuery() {
        BooleanQuery.Builder ret = null;
        /* generate query - AND phrase query */
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, size = listModel.getSize(); i < size; i++) {
            buffer.append("\"");
            buffer.append(listModel.getElementAt(i).getSuggestion());
            buffer.append("\"");
            if (i + 1 < size) {
                buffer.append(" AND ");
            }
        }
        /* */
        if (buffer.length() > 0) {
            /* need take care very much about analyzer */
            MultiFieldQueryParser parser = new MultiFieldQueryParser(
                    new String[]{ContentField.NAME, "dc:creator", "dc:title"},
                    new StandardAnalyzer());
            /* standard analyzed */
            try {
                ret = new BooleanQuery.Builder();
                ret.add(parser.parse(buffer.toString()), BooleanClause.Occur.FILTER);
            } catch (ParseException ex) {
                Logger.getLogger(SearchPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret != null ? ret.build() : null;
    }

    @Override
    public BooleanClause.Occur getCondition() {
        return BooleanClause.Occur.MUST;
    }

    class FacetAction extends AbstractAction {

        private JidePopup popup;

        public FacetAction() {
            super();
            putValue(SMALL_ICON, new ImageIcon(TextureManager.def().loadImage(
                    "16x16/facet_icon.png")));
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (popup == null) {
                popup = ShapedTransculentPopup.createPopup();
                popup.setBackground(Color.WHITE);
                popup.getContentPane().setLayout(new BorderLayout());
                popup.setOwner(jtfSearch);

                popup.getContentPane().add(facetPane, BorderLayout.CENTER);
            }
            /* */
            Dimension size = popup.getPreferredSize();
            size.width = jtfSearch.getWidth() / 2;
            popup.setPreferredPopupSize(size);
            popup.showPopup(0, jtfSearch.getHeight(), jtfSearch);
        }
    }
}
