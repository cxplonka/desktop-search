/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import java.io.IOException;
import javax.swing.text.JTextComponent;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SpellCheckerSuggestion extends AutoSuggestion {

    SpellChecker spellChecker;

    public SpellCheckerSuggestion(JTextComponent comp) throws IOException {
        super(comp);
        /* test */
//        LuceneIndexFileManager m = new LuceneIndexFileManager();
        Directory directory = new RAMDirectory();
        spellChecker = new SpellChecker(directory);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
//        spellChecker.indexDictionary(new LuceneDictionary(
//                m.getIndexSearcher().getIndexReader(), "field_content"), config, true);
        PlainTextDictionary index = new PlainTextDictionary(
                SimpleSuggestionService.class.getResourceAsStream("index.txt"));
        spellChecker.indexDictionary(index, config, true);
    }

    @Override
    protected boolean updateListData() {
        String value = textComp.getText();
        if (value.length() < 1) {
            return false;
        }
        try {
            String[] suggestions = spellChecker.suggestSimilar(value, 10, 0.2f);
            if (suggestions == null || suggestions.length == 0) {
                list.setListData(new String[0]);
            } else {
                list.setListData(suggestions);
            }
            return true;
        } catch (Exception e) {
            list.setListData(new String[0]);
            return true;
        }
    }

    @Override
    protected void acceptedListItem(String selected) {
        if (selected == null) {
            return;
        }
        textComp.setText(selected);
    }
}
