/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree;

import com.jidesoft.swing.CheckBoxTree;
import com.semantic.model.OModel;
import com.semantic.model.OntologyNode;
import com.semantic.swing.UIDefaults;
import com.semantic.swing.tree.dnd.OTreeTransferHandler;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import com.semantic.swing.tree.nodes.DefaultOntologyTreeNode;
import com.semantic.swing.tree.nodes.TreeNodeFactory;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class SemanticJTree extends CheckBoxTree implements Autoscroll {

    private OTreeTransferHandler cth;
    /* scroll stuff */
    private final int margin = 30;
    private int lastRowOver = -1;

    public SemanticJTree() {
        super();
        initTree();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                /* need for recalculate the node bounds in the basic treeui */
                ((BasicTreeUI) getUI()).setLeftChildIndent(0);
            }
        });
    }

    private void initTree() {
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 1, 1));
        setBackground(UIManager.getColor(UIDefaults.BACKGROUND_TREE));
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//        getCheckBoxTreeSelectionModel().setSingleEventMode(true);
        /* dnd support handler*/
        cth = new OTreeTransferHandler(this);
        /* our default renderer */
        setCellRenderer(new SemanticTreeCellRenderer());
        /* install root node */
        setModel(new SemanticTreeModel(new DefaultOntologyTreeNode(
                new OntologyNode("No Data"))));
    }

    public void setModel(OModel model) {
        /* create tree root model */
        AbstractOMutableTreeNode root = TreeNodeFactory.def().createTreeNode(model);
        setModel(new SemanticTreeModel(root));
    }

    @Override
    public Insets getAutoscrollInsets() {
        Rectangle outer = getBounds();
        Rectangle inner = getParent().getBounds();
        return new Insets(inner.y - outer.y + margin, inner.x - outer.x
                + margin, outer.height - inner.height - inner.y + outer.y
                + margin, outer.width - inner.width - inner.x + outer.x
                + margin);
    }

    @Override
    public void autoscroll(Point point) {
        int currentRow = this.getClosestRowForLocation(point.x, point.y);
        if (lastRowOver == -1) {
            lastRowOver = currentRow;
            return;
        }
        if (currentRow > lastRowOver) {
            scrollRowToVisible(currentRow + 1);
        } else if (currentRow > 0) {
            scrollRowToVisible(currentRow - 1);
        }
        lastRowOver = currentRow;
    }
}