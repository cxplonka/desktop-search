/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.suggest.Lookup;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public final class Suggestion {

    private final Lookup.LookupResult result;
    private String lucene = "";
    private int fieldCount = 0;

    public static Suggestion create(Lookup.LookupResult result, IndexReader reader, String[] fields) {
        Suggestion ret = new Suggestion(result);
        /* */
        if (reader != null && fields != null) {
            /* remove bold tag */
            String text = format(result.key.toString());
            try {
                for (String field : fields) {
                    int count = reader.docFreq(new Term(field, text));
                    if (count > 0) {
                        ret.lucene = field;
                        ret.fieldCount = count;
                    }
                }
            } catch (IOException ex) {
            }
        }
        return ret;
    }

    private Suggestion(Lookup.LookupResult result) {
        this.result = result;
    }

    public String getField() {
        return lucene;
    }

    public String getSuggestion() {
        return format(result.key.toString());
    }

    static String format(String value) {
        return value.replaceAll("<b>", "").replaceAll("</b>", "").trim();
    }

    @Override
    public String toString() {
        return String.format("<html>%s</html>", result.key);
    }
}
