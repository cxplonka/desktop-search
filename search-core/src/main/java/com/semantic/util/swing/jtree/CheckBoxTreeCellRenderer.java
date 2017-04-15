/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing.jtree;

import com.jidesoft.swing.NullTristateCheckBox;
import com.jidesoft.swing.TristateCheckBox;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class CheckBoxTreeCellRenderer extends JPanel implements TreeCellRenderer {

    protected TristateCheckBox _checkBox = null;
    protected JComponent _emptyBox = null;
    protected JCheckBox _protoType;
    protected TreeCellRenderer _delegate;

    public CheckBoxTreeCellRenderer() {
        this(new DefaultTreeCellRenderer());
    }

    public CheckBoxTreeCellRenderer(TreeCellRenderer renderer) {
        this(renderer, null);
    }

    public CheckBoxTreeCellRenderer(TreeCellRenderer renderer, TristateCheckBox checkBox) {
        _protoType = new TristateCheckBox();
        if (checkBox == null) {
            _checkBox = createCheckBox();
        } else {
            _checkBox = checkBox;
        }
        _emptyBox = (JComponent) Box.createHorizontalStrut(_protoType.getPreferredSize().width);
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        _delegate = renderer;
    }

    protected TristateCheckBox createCheckBox() {
        TristateCheckBox checkBox = new TristateCheckBox();
        checkBox.setOpaque(false);
        return checkBox;
    }

    public TreeCellRenderer getActualTreeRenderer() {
        return _delegate;
    }

    public void setActualTreeRenderer(TreeCellRenderer delegate) {
        _delegate = delegate;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        removeAll();
        _checkBox.setPreferredSize(new Dimension(_protoType.getPreferredSize().width, 0));
        _emptyBox.setPreferredSize(new Dimension(_protoType.getPreferredSize().width, 0));
        applyComponentOrientation(tree.getComponentOrientation());        

        if (value instanceof AbstractOMutableTreeNode) {
            AbstractOMutableTreeNode node = (AbstractOMutableTreeNode) value;
            updateCheckBoxState(_checkBox, node);
        }

        if (_delegate != null) {
            JComponent treeCellRendererComponent = (JComponent) _delegate.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);            
            remove(_emptyBox);            
            add(_checkBox, BorderLayout.BEFORE_LINE_BEGINS);
            add(treeCellRendererComponent);
            // copy the background and foreground for the renderer component
            setBackground(treeCellRendererComponent.getBackground());
            treeCellRendererComponent.setBackground(null);
            setForeground(treeCellRendererComponent.getForeground());
            treeCellRendererComponent.setForeground(null);
        }

        return this;
    }

    protected void updateCheckBoxState(TristateCheckBox checkBox, AbstractOMutableTreeNode node) {
        int state = isPartialSelected(node);
        switch (state) {
            case TristateCheckBox.STATE_MIXED:
                checkBox.setEnabled(false);
                break;
            default:
                checkBox.setEnabled(true);
                break;
        }
        checkBox.setState(state);
    }

    protected int isPartialSelected(AbstractOMutableTreeNode node) {
        if (node.getChildCount() == 0) {
            return node.isChecked() ? TristateCheckBox.STATE_SELECTED
                    : TristateCheckBox.STATE_UNSELECTED;
        }
        /* subnode checked */
        boolean allOn = true;
        boolean alternate = false;
        boolean first = ((AbstractOMutableTreeNode) node.getChildAt(0)).isChecked();
        for (int i = 0; i < node.getChildCount(); i++) {
            AbstractOMutableTreeNode child = (AbstractOMutableTreeNode) node.getChildAt(i);
            if (child.isLeaf()) {
                /* check for all leafs are checked */
                if (allOn && !child.isChecked()) {
                    allOn = false;
                }
                /* check if the leafs check marker are alternating */
                if (!alternate) {
                    alternate = child.isChecked() != first ? true : false;
                }
            }
        }
        return alternate ? TristateCheckBox.STATE_MIXED
                : allOn ? TristateCheckBox.STATE_SELECTED
                : TristateCheckBox.STATE_UNSELECTED;
    }
}