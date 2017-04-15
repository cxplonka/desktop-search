/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.suggest;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * snapping splitpane http://www.jroller.com/santhosh/feed/entries/rss?cat=%2FSwing
 * http://www.jroller.com/santhosh/entry/file_path_autocompletion
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SimpleSuggestionService {

    public static void main(String[] args) throws Exception {
        
        LevensteinDistance distance = new LevensteinDistance();
        System.out.println(distance.getDistance("ch", "christian"));        
        
        Directory directory = new RAMDirectory();
        SpellChecker spellChecker = new SpellChecker(directory, distance);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        
        PlainTextDictionary index = new PlainTextDictionary(new File("./index.txt").toPath());
        spellChecker.indexDictionary(index, config, true);        

        String wordForSuggestions = "christian";

        int suggestionsNumber = 5;

        String[] suggestions = spellChecker.suggestSimilar(wordForSuggestions, suggestionsNumber);

        if (suggestions != null && suggestions.length > 0) {
            for (String word : suggestions) {
                System.out.println("Did you mean: " + word);
            }
        } else {
            System.out.println("No suggestions found for word:" + wordForSuggestions);
        }
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setLayout(new FlowLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                JTextField field = new JTextField();
                field.setPreferredSize(new Dimension(300, 20));
                try {
                    AutoSuggestion suggest = new SpellCheckerSuggestion(field);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                frame.add(field);
                
                frame.setSize(400, 400);
                frame.setVisible(true);
            }
        });
    }
}