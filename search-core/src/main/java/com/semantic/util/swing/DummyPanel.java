/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import com.semantic.model.OModel;
import com.semantic.undo.DefaultUndoManager;
import com.semantic.undo.SmartUndoManager;
import com.semantic.undo.UndoableNodeModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author cplonka
 */
public class DummyPanel extends JPanel {
    
    private JLabel hits;
    private UndoableNodeModel undoableModel = new UndoableNodeModel();
    private DefaultUndoManager undoManager = new SmartUndoManager();
    
    public DummyPanel() {
        super(new BorderLayout());
        initOwnComponents();
    }
    
    private void initOwnComponents() {
        JPanel left = new JPanel(new GridLayout());
        left.setBorder(new EmptyBorder(0, 5, 0, 0));
        
        undoableModel.addUndoableEditListener(undoManager);
        JButton undo = new JButton(undoManager.getUndoAction());
        undo.setBorderPainted(false);
        undo.setHideActionText(true);
        undo.setOpaque(false);
        undo.setBackground(null);
        undo.setContentAreaFilled(false);
        left.add(undo);
        
        JButton redo = new JButton(undoManager.getRedoAction());
        redo.setBorderPainted(false);
        redo.setHideActionText(true);
        redo.setOpaque(false);
        redo.setBackground(null);
        redo.setContentAreaFilled(false);
        redo.setContentAreaFilled(false);        
        left.add(redo);

        add(left, BorderLayout.WEST);
        
        JPanel panel = new JPanel(new BorderLayout());
        hits = new JLabel("0 hits");
        hits.setForeground(Color.GRAY);
        hits.setHorizontalAlignment(JLabel.CENTER);
        panel.add(hits);
        add(panel);
    }
    
    public void setModel(OModel model) {
        undoableModel.setModel(model);
    }
    
    public void setHitText(String value) {
        hits.setText(value);
    }    
}
