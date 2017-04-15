/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefIterator;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class MultiFieldTermLuceneDirectory implements Dictionary {

    private final String[] fields;
    private final IndexReader reader;

    public MultiFieldTermLuceneDirectory(IndexReader reader, String... fields) {
        this.reader = reader;
        this.fields = fields;
    }

    @Override
    public InputIterator getEntryIterator() throws IOException {
        BytesRefIterator[] array = new BytesRefIterator[fields.length];
        for (int i = 0; i < array.length; i++) {
            Terms terms = MultiFields.getTerms(reader, fields[i]);
            if (terms != null) {
                array[i] = terms.iterator();
            } else {
                array[i] = BytesRefIterator.EMPTY;
            }
        }
        return new CombinedBytesRefIterator(array);
    }

    static class CombinedBytesRefIterator implements InputIterator {

        final BytesRefIterator[] iterators;
        private int idx = 0;

        public CombinedBytesRefIterator(BytesRefIterator... iterators) {
            this.iterators = iterators;
        }

        @Override
        public BytesRef next() throws IOException {
            BytesRef ret = null;
            if (idx < iterators.length) {
                ret = iterators[idx].next();
                if (ret == null && idx + 1 < iterators.length) {
                    /* switch to next */
                    ret = iterators[++idx].next();
                }
            }
            return ret;
        }

//        @Override
//        public Comparator<BytesRef> getComparator() {
//            Comparator<BytesRef> ret = null;
//            if (idx < iterators.length) {
//                ret = iterators[idx].getComparator();
//            }
//            return ret;
//        }

        @Override
        public long weight() {
            return 0;
        }

        @Override
        public BytesRef payload() {
            return null;
        }

        @Override
        public boolean hasPayloads() {
            return false;
        }

        @Override
        public Set<BytesRef> contexts() {
            return null;
        }

        @Override
        public boolean hasContexts() {
            return false;
        }
    }
}
