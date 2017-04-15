/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TransferableNode implements Transferable {

    public static final DataFlavor NODE_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "Node");
    private MutableTreeNode node;
    private DataFlavor[] flavors = {NODE_FLAVOR};

    public TransferableNode(MutableTreeNode nd) {
        node = nd;
    }

    @Override
    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor == NODE_FLAVOR) {
            return node;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor == NODE_FLAVOR) {
            return true;
        }
        return false;
    }
}
