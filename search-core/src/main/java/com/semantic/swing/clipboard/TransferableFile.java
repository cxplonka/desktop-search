/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TransferableFile implements Transferable {

    private List<File> fileList;

    public TransferableFile() {
        fileList = new ArrayList<File>();
    }

    public TransferableFile(File... files) {
        this();
        fileList.addAll(Arrays.asList(files));
    }

    public TransferableFile(Collection<File> files) {
        this();
        fileList.addAll(files);
    }
    
    public void addFile(File file){
        fileList.add(file);
    }
    
    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        if (flavor.equals(DataFlavor.javaFileListFlavor)) {
            return fileList;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.javaFileListFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.javaFileListFlavor);
    }
}