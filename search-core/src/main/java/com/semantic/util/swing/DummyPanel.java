/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import com.semantic.model.OModel;
import com.semantic.swing.preferences.PreferencesPanel;
import com.semantic.undo.DefaultUndoManager;
import com.semantic.undo.SmartUndoManager;
import com.semantic.undo.UndoableNodeModel;
import com.semantic.util.image.TextureManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DummyPanel extends JPanel implements MouseListener {

    private JLabel hits;
    private UndoableNodeModel undoableModel = new UndoableNodeModel();
    private DefaultUndoManager undoManager = new SmartUndoManager();

    private JDialog dialog;
    private final JLabel prefView = new JLabel(new ImageIcon(TextureManager.def().loadImage(
            "preferences-icon.png")));

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
        add(panel, BorderLayout.CENTER);

        JPanel east = new JPanel(new GridLayout());
        east.add(prefView);

        add(east, BorderLayout.EAST);

        prefView.setBorder(new EmptyBorder(0, 3, 0, 3));
        prefView.addMouseListener(this);
    }

    public void setModel(OModel model) {
        undoableModel.setModel(model);
    }

    public void setHitText(String value) {
        hits.setText(value);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (e.getSource() == prefView) {
                if (dialog == null) {
                    dialog = new JDialog(SwingUtilities.windowForComponent(this));
                    dialog.setModal(true);
                    dialog.setTitle("Preferences");
                    dialog.add(new PreferencesPanel());
                    dialog.setSize(600, 500);
                }
                //
                SwingUtils.centerComponent(SwingUtilities.windowForComponent(this), dialog);
                dialog.setVisible(true);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
