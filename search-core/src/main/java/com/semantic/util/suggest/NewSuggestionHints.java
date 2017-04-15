/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import com.jidesoft.hints.AbstractListIntelliHints;
import com.semantic.lucene.fields.image.ExifMakeField;
import com.semantic.lucene.fields.image.ExifModelField;
import com.semantic.lucene.util.IFieldProperty;
import java.io.IOException;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.text.JTextComponent;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class NewSuggestionHints extends AbstractListIntelliHints {

    private IndexReader indexReader;
    private AnalyzingInfixSuggester suggester;
    private final Suggestion dummy;
    /* suggestion fields */
    private final String[] fields = new String[]{
        "dc:creator" + IFieldProperty.EXT_SUGGEST,
        "dc:title" + IFieldProperty.EXT_SUGGEST,
        ExifModelField.NAME + IFieldProperty.EXT_SUGGEST,
        ExifMakeField.NAME + IFieldProperty.EXT_SUGGEST
    };
    private final DefaultListModel<Suggestion> listModel;

    public NewSuggestionHints(JTextComponent jtc, DefaultListModel<Suggestion> listModel) {
        super(jtc);
        this.listModel = listModel;
        setFollowCaret(true);
        dummy = Suggestion.create(new Lookup.LookupResult("no suggestion", 1), null, fields);
    }

    @Override
    protected JList createList() {
        JList list = super.createList();
        list.setCellRenderer(new AutoCompleteListCellRenderer());
        return list;
    }

    public void setIndexReader(IndexReader indexReader) {
        this.indexReader = indexReader;
        /* only build 1 time */
        if (suggester == null) {
            Dictionary directory = new MultiFieldTermLuceneDirectory(
                    indexReader,
                    fields);
            try {
                suggester = new AnalyzingInfixSuggester(
//                        new File("./suggestidx"),
                        new RAMDirectory(),
                        new StandardAnalyzer());
                suggester.build(directory);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean updateHints(Object o) {
        String[] words = o.toString().split("\\s");
        if (words.length > 0 && indexReader != null) {
            /* suggest last word */
            List<Lookup.LookupResult> ret;
            try {
                ret = suggester.lookup(o.toString(), 5, true, true);
                if (!ret.isEmpty()) {
                    Suggestion[] suggestion = new Suggestion[ret.size()];
                    for (int i = 0; i < suggestion.length; i++) {
                        suggestion[i] = Suggestion.create(ret.get(i), indexReader, fields);
                    }
                    setListData(suggestion);
                } else {
                    setListData(new Suggestion[]{dummy});
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void acceptHint(Object o) {
        if (o != null && !o.equals(dummy)) {
            Suggestion s = (Suggestion) o;
            String word = s.getSuggestion();
            /* */
            if (!word.isEmpty()) {
                listModel.addElement(s);
            }
        }
    }
}
