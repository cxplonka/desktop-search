/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test.topterms;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SimpleCollector;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TopTermsCollector extends SimpleCollector {

    private final String _field;
    private IndexReader _indexReader;
    private final TObjectIntHashMap _count = new TObjectIntHashMap();

    public TopTermsCollector(String _field) {
        this._field = _field;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
    }

    @Override
    public void collect(int i) throws IOException {
        /* only matched documents from the query */
        Terms termVector = _indexReader.getTermVector(i, _field);
        if (termVector != null) {
            TermsEnum enu = termVector.iterator();
            while (enu.next() != null) {
                _count.adjustOrPutValue(enu.term().utf8ToString(), 1, 1);
            }
        }
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        super.doSetNextReader(context);
        this._indexReader = context.reader();
    }

    public TreeMap<Integer, String> getTopTerms() {
        TreeMap<Integer, String> sorted = new TreeMap<Integer, String>(new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1.equals(o2)) {
                    return 1;
                }
                return o1.compareTo(o2);
            }
        });
        for (TObjectIntIterator<String> it = _count.iterator(); it.hasNext();) {
            it.advance();
            sorted.put(it.value(), it.key());
        }
        return sorted;
    }

    @Override
    public boolean needsScores() {
        return false;
    }
}
