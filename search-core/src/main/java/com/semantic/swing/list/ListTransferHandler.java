/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.list;

import com.semantic.lucene.fields.FileNameField;
import com.semantic.swing.clipboard.TransferableFile;
import java.awt.datatransfer.Transferable;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ListTransferHandler extends TransferHandler {

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            JList list = (JList) c;
            TransferableFile transferable = new TransferableFile();            
            for (Object select : list.getSelectedValuesList()) {
                if (select instanceof Document) {
                    Document doc = (Document) select;
                    transferable.addFile(new File(doc.get(FileNameField.NAME)));
                }
            }
            return transferable;
        }
        return super.createTransferable(c);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }
}