/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.dnd;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public abstract class AbstractTreeTransferHandler implements DragGestureListener, DragSourceListener, DropTargetListener {

    private JTree tree;
    private DragSource dragSource;
    private DropTarget dropTarget;
    private boolean drawMarker;
    private Component oldGlassPane;
    private Point from, to;
    // glasspane on which visual clues are drawn 
    private JPanel glassPane = new JPanel() {

        @Override
        public void paint(Graphics g) {
            g.setColor(Color.red);
            if (from == null || to == null) {
                return;
            }
            int x1 = from.x;
            int x2 = to.x;
            int y1 = from.y;

            // line 
            g.drawLine(x1 + 2, y1, x2 - 2, y1);
            g.drawLine(x1 + 2, y1 + 1, x2 - 2, y1 + 1);

            // right 
            g.drawLine(x1, y1 - 2, x1, y1 + 3);
            g.drawLine(x1 + 1, y1 - 1, x1 + 1, y1 + 2);

            // left 
            g.drawLine(x2, y1 - 2, x2, y1 + 3);
            g.drawLine(x2 - 1, y1 - 1, x2 - 1, y1 + 2);
        }
    };
    // size of hotspot used to find 
    // the whether user wants to insert element 
    private int hotspot = 5;
    // droppath - subclasses can access this in to accept/reject drop 
    protected TreePath treePath = null;
    // null means replace node at treePath 
    // true means insert node before treePath 
    // false means insert node after treePath 
    // subclasses can access this in drop 
    protected Boolean before = null;

    /**
     * move action support
     * @param tree
     * @param drawIcon
     */
    protected AbstractTreeTransferHandler(JTree tree, boolean drawMarker) {
        this.tree = tree;
        this.drawMarker = drawMarker;
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_MOVE, this);
        dropTarget = new DropTarget(tree, DnDConstants.ACTION_MOVE, this);
    }

    /* Methods for DragSourceListener */
    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        /* nodes fire self events to the root */
    }

    @Override
    public final void dragEnter(DragSourceDragEvent dsde) {
        int action = dsde.getDropAction();
        if (action == DnDConstants.ACTION_MOVE) {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
        } else {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
        }
    }

    @Override
    public final void dragOver(DragSourceDragEvent dsde) {
        int action = dsde.getDropAction();
        if (action == DnDConstants.ACTION_MOVE) {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
        } else {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
        }
    }

    @Override
    public final void dropActionChanged(DragSourceDragEvent dsde) {
        int action = dsde.getDropAction();
        if (action == DnDConstants.ACTION_MOVE) {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
        } else {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
        }
    }

    @Override
    public final void dragExit(DragSourceEvent dse) {
        dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    }

    protected void createDragComponent(TreePath picked) {
        /* empty */
    }

    protected void updateDragComponent(Point point) {
    }

    protected void cleanDragComponent() {
    }

    private void resetGlassPane(DropTargetEvent dte) {
        JTree targetTree = (JTree) dte.getDropTargetContext().getComponent();
        JRootPane rootPane = targetTree.getRootPane();
        rootPane.setGlassPane(oldGlassPane);
        oldGlassPane.setVisible(false);
        rootPane.repaint();
    }

    /* Methods for DragGestureListener */
    @Override
    public final void dragGestureRecognized(DragGestureEvent dge) {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            MutableTreeNode draggedNode = (MutableTreeNode) path.getLastPathComponent();
            /* only allow special nodes */
            if (dragAllow(tree, draggedNode, draggedNode)) {
                /* create component for mark the drag status */
                if (drawMarker) {
                    createDragComponent(path);
                }
                dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop,
                        new TransferableNode(draggedNode), this);
            }
        }
    }

    private MutableTreeNode getTransferableNode(Transferable transferable) {
        try {
            if (transferable.isDataFlavorSupported(TransferableNode.NODE_FLAVOR)) {
                return (MutableTreeNode) transferable.getTransferData(TransferableNode.NODE_FLAVOR);
            }
        } catch (Exception e) {
        }
        return null;
    }

    /* Methods for DropTargetListener */
    @Override
    public final void dragEnter(DropTargetDragEvent dtde) {
        Point pt = dtde.getLocation();
        int action = dtde.getDropAction();
        if (drawMarker) {
            updateDragComponent(pt);
        }
        /* visual feedback */
        JRootPane rootPane = tree.getRootPane();
        oldGlassPane = rootPane.getGlassPane();
        rootPane.setGlassPane(glassPane);
        glassPane.setOpaque(false);
        glassPane.setVisible(true);
        /* */
        MutableTreeNode draggedNode = getTransferableNode(dtde.getTransferable());
        if (draggedNode != null) {
            if (isDropPossible(tree, draggedNode, action, pt)) {
                updateLine(tree, pt);
                /* accept */
                dtde.acceptDrag(action);
            } else {
                dtde.rejectDrag();
            }
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public final void dragExit(DropTargetEvent dte) {
        resetGlassPane(dte);
        if (drawMarker) {
            cleanDragComponent();
        }
    }

    @Override
    public final void dragOver(DropTargetDragEvent dtde) {
        Point pt = dtde.getLocation();
        int action = dtde.getDropAction();
        if (drawMarker) {
            updateDragComponent(pt);
        }
        /* */
        MutableTreeNode draggedNode = getTransferableNode(dtde.getTransferable());
        if (draggedNode != null) {
            if (isDropPossible(tree, draggedNode, action, pt)) {
                updateLine(tree, pt);
                dtde.acceptDrag(action);
            } else {
                dtde.rejectDrag();
            }
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public final void dropActionChanged(DropTargetDragEvent dtde) {
        Point pt = dtde.getLocation();
        int action = dtde.getDropAction();
        if (drawMarker) {
            updateDragComponent(pt);
        }
        /* */
        MutableTreeNode draggedNode = getTransferableNode(dtde.getTransferable());
        if (draggedNode != null) {
            if (isDropPossible(tree, draggedNode, action, pt)) {
                dtde.acceptDrag(action);
            } else {
                dtde.rejectDrag();
            }
        } else {
            dtde.rejectDrag();
        }
    }

    @Override
    public final void drop(DropTargetDropEvent dtde) {
        try {
            resetGlassPane(dtde);
            if (drawMarker) {
                cleanDragComponent();
            }
            int action = dtde.getDropAction();
            Point pt = dtde.getLocation();
            /* */
            MutableTreeNode draggedNode = getTransferableNode(dtde.getTransferable());
            if (draggedNode != null) {
                if (isDropPossible(tree, draggedNode, action, pt)) {                    
                    MutableTreeNode newParentNode = dragTarget(tree, draggedNode, action, pt);
                    if (dropAllow(tree, draggedNode, newParentNode, action, pt)) {
                        dtde.acceptDrop(action);
                        dtde.dropComplete(true);
                    }
                }
            } else {
                dtde.rejectDrop();
                dtde.dropComplete(false);
            }
        } catch (Exception e) {
            dtde.rejectDrop();
            dtde.dropComplete(false);
        }
    }

    private void updateLine(JTree tree, Point pt) {
        treePath = tree.getPathForLocation(pt.x, pt.y);
        if (treePath == null) {
            from = to = null;
            before = null;
            tree.clearSelection();
        } else {
            Rectangle bounds = tree.getPathBounds(treePath);
            if (pt.y <= bounds.y + hotspot) {
                from = bounds.getLocation();
                to = new Point(from.x + bounds.width, from.y);
                before = Boolean.TRUE;
            } else if (pt.y >= bounds.y + bounds.height - hotspot) {
                from = new Point(bounds.x, bounds.y + bounds.height);
                to = new Point(from.x + bounds.width, from.y);
                before = Boolean.FALSE;
            } else {
                from = to = null;
                before = null;
            }
            if (from != null && to != null) {
                from = SwingUtilities.convertPoint(tree, from, glassPane);
                to = SwingUtilities.convertPoint(tree, to, glassPane);
                tree.clearSelection();
            } else {
                tree.setSelectionPath(treePath);
            }
        }
        glassPane.getRootPane().repaint();
    }

    public abstract boolean dragAllow(JTree tree, MutableTreeNode draggedNode, MutableTreeNode newParentNode);

    public abstract boolean isDropPossible(JTree target, MutableTreeNode draggedNode, int action, Point location);

    public abstract MutableTreeNode dragTarget(JTree target, MutableTreeNode draggedNode, int action, Point location);
    
    public abstract boolean dropAllow(JTree tree, MutableTreeNode draggedNode, MutableTreeNode newParentNode, int action, Point location);
}