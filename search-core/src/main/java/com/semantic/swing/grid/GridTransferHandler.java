/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.grid;

import com.guigarage.jgrid.JGrid;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.model.OntologyNode;
import com.semantic.model.filter.ORGBFilter;
import com.semantic.swing.clipboard.TransferableFile;
import com.semantic.swing.tree.dnd.TransferableNode;
import com.semantic.swing.tree.nodes.model.ONodeTreeNode;
import com.semantic.util.image.ImageUtil;
import com.semantic.util.lazy.LazyList;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import javax.swing.ListSelectionModel;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class GridTransferHandler implements DragGestureListener, DragSourceListener {

    private final JGrid grid;
    private File currentFile;
    private ORGBFilter currentNode;
    private final DragSource dragSource;

    public GridTransferHandler(JGrid grid) {
        this.grid = grid;
        dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(grid, DnDConstants.ACTION_MOVE, this);
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        int selection = grid.getSelectedIndex();
        if (selection > 0) {
            LazyDocumentListModel m = (LazyDocumentListModel) grid.getModel();
            Document doc = m.getLazyList().get(grid.getSelectedIndex());
            /* */
            currentFile = new File(doc.get(FileNameField.NAME));
            if (ImageUtil.isSupportedExt(currentFile)) {
                /* rgb filter */
                currentNode = new ORGBFilter(currentFile.getName());
                /* all stuff will be handled from the tree transfer handler */
                Transferable transferable = dragTransferable(
                        getSelections(grid.getSelectionModel()), m.getLazyList());
                /* for desktop transfer */
//                dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop,
//                        transferable, this);
                /* for tree transfer */
                dragSource.startDrag(dge, DragSource.DefaultMoveNoDrop,
                        new TransferableNode(new ONodeTreeNode<OntologyNode>(
                        currentNode)), this);
            }
        }
    }

    private Transferable dragTransferable(int[] id, LazyList<Document> list) {
        TransferableFile transferable = new TransferableFile();
        for (int i : id) {
            if (i != -1) {
                Document doc = list.get(i);
                transferable.addFile(new File(doc.get(FileNameField.NAME)));
            }
        }
        return transferable;
    }

    private int[] getSelections(ListSelectionModel selectionModel) {
        int min = selectionModel.getMinSelectionIndex();
        //min = max for one selection
        int max = selectionModel.getMaxSelectionIndex() + 1;
        //-1 = no selection
        if (min != -1) {
            int[] docIDs = new int[max - min];
            //read selected document id's
            for (int i = min; i < max; i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    //fetch document
                    docIDs[i - min] = i;
                } else {
                    docIDs[i - min] = -1;
                }
            }
            return docIDs;
        }
        return new int[0];
    }

    @Override
    public final void dragEnter(DragSourceDragEvent dsde) {
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
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
        dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        /* calculate the mean value only after success drop */
        if (dsde.getDropSuccess()) {
            try {
                /* caluclate the rgb mean value */
                currentNode.setMeanRGB(ImageUtil.analyzeRGB(currentFile));
            } catch (IOException ex) {
                currentFile = null;
                currentNode = null;
            }
        }
    }
}