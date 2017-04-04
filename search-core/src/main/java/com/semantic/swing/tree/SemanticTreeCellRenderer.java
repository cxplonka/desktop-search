/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree;

import com.jidesoft.swing.TristateCheckBox;
import com.semantic.lucene.facet.FacetQueryHitCountCollector;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import com.semantic.util.Range;
import java.awt.*;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author cplonka
 */
public class SemanticTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final Color highlightColor = new Color(200, 0, 0);
    private final int _checkBoxWidth = new TristateCheckBox().getPreferredSize().width;
    private int _containerWidth = 0;
    private int _x = 0;
    private static final int maxw = 40;
    private long _facet_count = 0;
    private long _facet_root_count = 0;
    private final Range _range = new Range();

    public SemanticTreeCellRenderer() {
        super();
        setOpaque(false);
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return null;
    }

    @Override
    public Color getBackground() {
        return null;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        DefaultTreeCellRenderer renderer
                = (DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
//        renderer.setBorder(new LineBorder(Color.RED));
        /* because tree width will change in order to node bounds */
        if (tree.getParent() != null) {
            _containerWidth = tree.getParent().getWidth();
        }
        /* set tree icon in renderer */
        if (value instanceof AbstractOMutableTreeNode) {
            AbstractOMutableTreeNode node = (AbstractOMutableTreeNode) value;
            /* layout magic */
            Insets i = tree.getBorder().getBorderInsets(tree);
            int level = node.getLevel() + 4;
            _x = _checkBoxWidth * level + i.left;
            /* 4 - size between the levels - 10 magic number!? */
            _x -= level * 10;
            /* */
            renderer.setIcon(node.getTreeNodeIcon());
            OntologyNode onode = node.getUserObject();
            /* */
            OntologyNode root = (OntologyNode) onode.getRoot();
            _range.setBounds((double) 0, (double) root.get(FacetQueryHitCountCollector.KEY_FACET_COUNT));
            _facet_count = onode.get(FacetQueryHitCountCollector.KEY_FACET_COUNT);
            _facet_root_count = onode.get(FacetQueryHitCountCollector.KEY_FACET_ROOT_COUNT);
            /* property from IQueryGenerator strike through */
            if (onode.get(IQueryGenerator.BOOLEAN_CLAUSE).equals(IQueryGenerator.CLAUSE.NOT)) {
                setText(String.format("<html><strike>%s</strike></html>", value.toString()));
            }
            /* property from IQueryGenerator strike through */
            if (onode.get(AbstractOMutableTreeNode.KEY_NODE_HIGHLIGHTED)) {
                setForeground(highlightColor);
            }
        }
        return renderer;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        /* take care of checkbox or not!!! */
        d.width = _containerWidth - _x;
        return d;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        /* layout and draw facet count graphics */
        String value = Long.toString(_facet_count);
        if (_facet_root_count > 0) {
            value += String.format("|%s", _facet_root_count);
        }

        int sh = g.getFontMetrics().getHeight();
        int sw = g.getFontMetrics().stringWidth(value) + 3;
        int ss = (int) (getHeight() - ((getHeight() - sh) * 2));

        g.setColor(Color.GRAY);
        g.drawString(value, getWidth() - (sw + maxw), ss);
        double n = _range.normalize(_facet_count);
        int h = getHeight() / 2;
        g.fillRect(getWidth() - maxw, h / 2, (int) (n * maxw), h);        
    }
}