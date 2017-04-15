/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import com.semantic.ApplicationContext;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DirectSpellCheckerTest extends JFrame {

    IndexSearcher indexSearcher;

    public DirectSpellCheckerTest() throws HeadlessException {
        super();
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(200, 200);
        setLocation(400, 400);

        try {
            loadIndex();
        } catch (Exception ex) {
        }

        JTextField field = new JTextField();
        SuggestionHints suggestionHints = new SuggestionHints( field);
        suggestionHints.setIndexReader(indexSearcher.getIndexReader());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(field, BorderLayout.NORTH);
        add(panel);
    }

    private void loadIndex() throws Exception {
        Directory index = FSDirectory.open(new File(ApplicationContext.ISEARCH_HOME + "/.lucene").toPath());
        /* setup indexwriter */
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setRAMBufferSizeMB(64);
        IndexWriter indexWriter = new IndexWriter(index, config);

        indexSearcher = new IndexSearcher(DirectoryReader.open(indexWriter));

//        DirectSpellChecker s = new DirectSpellChecker();
//        SuggestWord[] words = s.suggestSimilar(new Term(TextLuceneFileHandler.FIELD_FILE_CONTENT.getField(), "chr"), 5, 
//                indexSearcher.getIndexReader(), SuggestMode.SUGGEST_ALWAYS);
//        
//        System.out.println(words.length);
//        for (SuggestWord word : words) {
//            System.out.println(word.string);
//        }
    }

    public static void main(String[] arg) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                DirectSpellCheckerTest test = new DirectSpellCheckerTest();
                test.setVisible(true);
            }
        });
    }
}
