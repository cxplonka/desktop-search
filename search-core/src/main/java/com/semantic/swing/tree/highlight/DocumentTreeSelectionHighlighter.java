/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.highlight;

import com.semantic.ApplicationContext;
import com.semantic.lucene.facet.FacetQueryHitCountCollector;
import com.semantic.model.OModel;
import com.semantic.model.OntologyNode;
import com.semantic.swing.DocumentPropertyView;
import com.semantic.swing.table.LazyDocumentListService;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.util.LongBitSet;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DocumentTreeSelectionHighlighter implements ListSelectionListener {

    private final List<Document> documents;
    private final JTree tree;
    private final DocumentPropertyView docView;

    public DocumentTreeSelectionHighlighter(JTree tree, List<Document> documents, DocumentPropertyView docView) {
        this.documents = documents;
        this.tree = tree;
        this.docView = docView;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        /* table selection || result selection */
        if (!e.getValueIsAdjusting()) {
            List<Document> selections = new ArrayList<Document>(1);

            ListSelectionModel selectionModel = (ListSelectionModel) e.getSource();
            int min = selectionModel.getMinSelectionIndex();
            /* min = max for one selection */
            int max = selectionModel.getMaxSelectionIndex() + 1;
            int[] docIDs = new int[max - min];
            /* -1 = no selection */
            if (min != -1) {
                for (int i = min; i < max; i++) {
                    if (selectionModel.isSelectedIndex(i)) {
                        /* fetch document */
                        Document doc = documents.get(i);
                        docIDs[i - min] = ((IntPoint) doc.getField(
                                LazyDocumentListService.FIELD_SESSION_DOCID)).numericValue().intValue();
                        selections.add(doc);
                    }
                }
            } else {
                docIDs = new int[0];
            }
            /* evaluate model tree */
            OModel model = ApplicationContext.instance().get(ApplicationContext.MODEL);
            evaluate(model, docIDs);

            /* repaint */
            tree.repaint();

            /* document field overview */
            docView.setSelectedDocuments(selections.toArray(new Document[selections.size()]));
        }
    }

    private void evaluate(OntologyNode node, int[] docIDs) {
        for (int i = 0; i < node.getNodeCount(); i++) {
            /* first evaluate all children's before evaluate the parent */
            evaluate(node.getChildAt(i), docIDs);
        }
        /* query hit */
        if (node.has(FacetQueryHitCountCollector.KEY_BITSET)) {
            LongBitSet querySet = node.get(FacetQueryHitCountCollector.KEY_BITSET);
            node.set(AbstractOMutableTreeNode.KEY_NODE_HIGHLIGHTED, false);
            for (int id : docIDs) {
                /* if the document is hit, highlight the node */
                if (querySet.get(id)) {
                    node.set(AbstractOMutableTreeNode.KEY_NODE_HIGHLIGHTED, true);
                }
            }
        }
    }
}
