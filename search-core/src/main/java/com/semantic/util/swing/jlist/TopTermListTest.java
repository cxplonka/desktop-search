/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jlist;

import com.semantic.util.test.topterms.TopTerm;
import java.awt.BorderLayout;
import javax.swing.*;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TopTermListTest extends JFrame {

    private JList _previewList;

    public TopTermListTest() {
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        init();
    }

    private void init() {
        JScrollPane _scroll = new JScrollPane(
                _previewList = new JList(new TopTermListModel()),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        _scroll.setBorder(null);

        _previewList.setOpaque(false);        
        _previewList.setCellRenderer(new TopTermListCellRenderer());
        _previewList.setVisibleRowCount(1);
        _previewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _previewList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        _scroll.setViewportView(_previewList);
        
        RemoveableListManager removeableListManager = new RemoveableListManager(_previewList);

        add(_previewList, BorderLayout.CENTER);
    }

    class TopTermListModel extends DefaultListModel{

        TopTerm[] terms = {
            new TopTerm("field", "audi", 2),
            new TopTerm("field", "vw", 2),
            new TopTerm("field", "mercedes", 2)
        };

        public TopTermListModel() {
            super();
            for(TopTerm topTerm : terms){
                addElement(topTerm);
            }
        }
    }
    
    public static void main(String[] arg) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TopTermListTest list = new TopTermListTest();
                list.setSize(400, 400);
                list.setVisible(true);
            }
        });
    }
}