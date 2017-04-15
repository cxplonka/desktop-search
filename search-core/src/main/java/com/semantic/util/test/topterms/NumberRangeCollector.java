/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test.topterms;

import com.semantic.lucene.util.IFieldProperty;
import com.semantic.util.Range;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.SimpleCollector;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class NumberRangeCollector extends SimpleCollector {

    private final String _field;
    private IndexReader _indexReader;
    private final Range _range = new Range(Float.MAX_VALUE, -Float.MAX_VALUE);

    public NumberRangeCollector(IFieldProperty<Long> key) {
        this._field = key.getName();
    }

    public Range getRange() {
        return _range;
    }

    @Override
    public void setScorer(Scorer scorer) throws IOException {
    }

    @Override
    public void collect(int i) throws IOException {
        /* only matched documents from the query */
        Document doc = _indexReader.document(i);
        IndexableField field = doc.getField(_field);
        if (field != null) {
            Number number = field.numericValue();
            if (number != null) {
                collect(i, number);
            }
        }
    }

    public void collect(int i, Number number) {
        _range.add(number.longValue());
    }

    @Override
    protected void doSetNextReader(LeafReaderContext context) throws IOException {
        super.doSetNextReader(context);
        this._indexReader = context.reader();
    }

    @Override
    public boolean needsScores() {
        return false;
    }
}