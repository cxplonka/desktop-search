/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.facet;

import com.semantic.model.IQueryGenerator;
import com.semantic.model.OGroup;
import com.semantic.model.OntologyNode;
import com.semantic.util.property.IPropertyKey;
import com.semantic.util.property.PropertyKey;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SimpleCollector;
import org.apache.lucene.util.LongBitSet;

/**
 * wordnet: https://gist.github.com/562776
 * idea from
 * http://sujitpal.blogspot.com/2010/02/handling-lucene-hits-deprecation-in.html
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FacetQueryHitCountCollector {

    private final IndexSearcher searcher;
    private final LongBitSet rootSet;
    private final LongBitSet subSet;
    /** our bitset property for the nodes, not persist and no change event */
    public static IPropertyKey<LongBitSet> KEY_BITSET = PropertyKey.createWithOut("key_bitset",
            LongBitSet.class, null);
    /** facet count for this node query */
    public static IPropertyKey<Long> KEY_FACET_COUNT = PropertyKey.createWithOut(
            "key_facet_count", Long.class, 0l);
    /** count for matching with all documents(subquery) */
    public static IPropertyKey<Long> KEY_FACET_ROOT_COUNT = PropertyKey.createWithOut(
            "key_facet_root_count", Long.class, 0l);

    public FacetQueryHitCountCollector(IndexSearcher searcher, Query rootQuery) {
        this.searcher = searcher;
        /* */
        int maxDoc = searcher.getIndexReader().maxDoc();
        rootSet = new LongBitSet(maxDoc);
        subSet = new LongBitSet(maxDoc);
        updateBitSet(rootQuery, searcher, rootSet);
    }

    private void updateBitSet(Query query, IndexSearcher searcher, final LongBitSet set) {
        try {
            searcher.search(query, new SimpleCollector() {

                private int docBase;

                @Override
                public void collect(int doc) {
                    set.set(doc + docBase);
                }

                @Override
                protected void doSetNextReader(LeafReaderContext context) throws IOException {
                    super.doSetNextReader(context);
                    this.docBase = context.docBase;
                }

                @Override
                public boolean needsScores() {
                    return false;
                }
            });
        } catch (Exception ex) {
        }
    }

    public void facetCount(OntologyNode node, boolean first, boolean last) {
        for (int i = 0; i < node.getNodeCount(); i++) {
            /* first evaluate all children's before evaluate the parent */
            facetCount(node.getChildAt(i), i == 0, i == node.getNodeCount() - 1);
        }
        /* clear group bitset before analysing */
        subSet.clear(0, subSet.length());
        /* sum up for parent node */
        if (node instanceof OGroup) {
            OGroup group = (OGroup) node;
            if (group.has(KEY_BITSET) && group.get(
                    KEY_BITSET).length() == subSet.length()) {
                subSet.or(group.get(KEY_BITSET));
            }
        }
        /* query hit, query nodes */
        if (node instanceof IQueryGenerator) {
            /* create or clear current query bitset */
            LongBitSet querySet = clear(node, subSet.length());
            /* sub-root-query matching, evaluate */
            updateBitSet(((IQueryGenerator) node).createQuery(), searcher, subSet);
            /* evaluate query subset */
            querySet.or(subSet);
            /* hits in all documents from this subquery */
            node.set(KEY_FACET_ROOT_COUNT, querySet.cardinality());
            /* hits for the query with view to the rootquery */
            subSet.and(rootSet);
            node.set(KEY_FACET_COUNT, subSet.cardinality());
        }
        /* add to levelset */
        if (node.getParent() instanceof OGroup) {
            OGroup parent = (OGroup) node.getParent();
            LongBitSet parentSet = parent.get(KEY_BITSET);
            /* clear */
            if (first) {
                parentSet = clear(parent, subSet.length());
            }
            /* modify reference */
            parentSet.or(subSet);
        }
        /* after all, set parent name - sum */
        if (last) {
            OGroup parent = (OGroup) node.getParent();
            if (parent.has(KEY_BITSET)) {
                parent.set(KEY_FACET_COUNT,
                        parent.get(KEY_BITSET).cardinality());
            }
        }
    }

    private LongBitSet clear(OntologyNode node, long newSize) {
        LongBitSet querySet;
        if (node.has(KEY_BITSET)) {
            querySet = node.get(KEY_BITSET);
            /* not same size, recreate query bitSet */
            if (querySet.length()!= subSet.length()) {
                node.set(KEY_BITSET, querySet = new LongBitSet(newSize));
            } else {
                querySet.clear(0, querySet.length());
            }
        } else {
            /* not contained, create */
            node.set(KEY_BITSET, querySet = new LongBitSet(newSize));
        }
        return querySet;
    }
}
