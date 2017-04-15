/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.test.topterms;

import java.io.StringReader;
import java.io.StringWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;

/**
 * http://stackoverflow.com/questions/5233749/how-to-use-lucene-highlighter-with-phrasequery
 * term count -
 * http://www.searchworkings.org/blog/-/blogs/introducing-lucene-index-doc-values
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TermHightlight {

    public static String hightlight(Query query, String fieldName, String text, Analyzer analyzer) throws Exception {
        SimpleHTMLFormatter formatter =
                new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");

        /* text null - text not stored, must be read everytime */
        TokenStream tokens = analyzer.tokenStream(fieldName, new StringReader(text));

        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));
        String result = highlighter.getBestFragments(tokens, text, 2, "...");
        if (!result.isEmpty()) {
            StringWriter writer = new StringWriter();
            writer.write("<html>");
            writer.write("<style>\n"
                    + ".highlight {\n"
                    + " background: yellow;\n"
                    + "}\n"
                    + "</style>");
            writer.write("<body>...");
            writer.write(result);
            writer.write("...</body></html>");
            writer.close();
            return writer.getBuffer().toString();
        }
        return null;
    }
}