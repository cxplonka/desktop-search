/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import com.jidesoft.hints.AbstractListIntelliHints;
import com.semantic.lucene.fields.ContentField;
import java.io.IOException;
import javax.swing.text.JTextComponent;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spell.*;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SuggestionHints extends AbstractListIntelliHints {

    private IndexReader indexReader;
    private final DirectSpellChecker spellChecker = new DirectSpellChecker();
    private final Suggestion dummy;

    public SuggestionHints(JTextComponent jtc) {
        super(jtc);
        setFollowCaret(true);
        SuggestWord suggestWord = new SuggestWord();
        suggestWord.string = "no suggestion";
        dummy = new Suggestion(suggestWord);

        spellChecker.setMinQueryLength(1);
        spellChecker.setMinQueryLength(3);
        spellChecker.setMaxInspections(25);
        spellChecker.setComparator(new SuggestWordScoreComparator());
        spellChecker.setDistance(new LevensteinDistance());
    }

    public void setIndexReader(IndexReader indexReader) {
        this.indexReader = indexReader;
    }

    @Override
    public boolean updateHints(Object o) {
        String text = o.toString();
        String[] words = text.split("\\s");
        if (words.length > 0 && indexReader != null) {
            try {
                String last = words[words.length - 1];

                SuggestWord[] suggest = spellChecker.suggestSimilar(
                        new Term(ContentField.NAME, last), 5,
                        indexReader);

                if (suggest.length > 0) {
                    Suggestion[] suggestion = new Suggestion[suggest.length];
                    for (int i = 0; i < suggestion.length; i++) {
                        suggestion[i] = new Suggestion(suggest[i]);
                    }
                    setListData(suggestion);
                } else {
                    setListData(new Suggestion[]{dummy});
                }
            } catch (IOException ex) {
                setListData(new Suggestion[]{dummy});
            }
        }
        return true;
    }

    @Override
    public void acceptHint(Object o) {
        if (o != null && !o.equals(dummy)) {
            /* */
            String value = getTextComponent().getText();
            Suggestion s = (Suggestion) o;
            String word = s.word.string;
            /* */
            String[] words = value.split("\\s");
            if (words.length > 0) {
                /* update string */
                words[words.length - 1] = word;
                String text = "";
                for (String w : words) {
                    text += w + " ";
                }
                getTextComponent().setText(text);
            }
        }
    }

    class Suggestion {

        SuggestWord word;

        public Suggestion(SuggestWord word) {
            this.word = word;
        }

        @Override
        public String toString() {
            return String.format("%s [%s - %s]", word.string, word.freq, word.score);
        }
    }
}