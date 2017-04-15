/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.table;

import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.util.lazy.LazyList;
import com.semantic.util.lazy.LazyListTableModel;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LazyDocumentsTableModel extends LazyListTableModel<Document> {

    public LazyDocumentsTableModel(LazyList<Document> lazyList) {
        super(lazyList);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ImageIcon.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public Object getColumnValue(int rowIndex, int columnIndex, Document listElement) {
        Object ret = null;
        switch (columnIndex) {
            case 0:
                ret = FileSystemView.getFileSystemView().getSystemIcon(
                        new File(getColumnValue(rowIndex, 1, listElement).toString()));
                break;
            case 1:
                ret = listElement.get(FileNameField.NAME);
                break;
            case 2:
                ret = listElement.get(MimeTypeField.NAME);
                break;
        }
        return ret;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        String ret = "";
        switch (column) {
            case 0:
                ret = "Position";
                break;
            case 1:
                ret = "File Name";
                break;
            case 2:
                ret = "MIME Type";
                break;
        }
        return ret;
    }
}