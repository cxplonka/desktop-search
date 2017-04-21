/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.table;

import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.lucene.fields.SizeField;
import com.semantic.util.DateUtil;
import com.semantic.util.FileUtil;
import com.semantic.util.lazy.LazyList;
import com.semantic.util.lazy.LazyListTableModel;
import java.io.File;
import java.util.Date;
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
    public Object getColumnValue(int rowIndex, int columnIndex, Document doc) {
        Object ret = null;
        File file = new File(doc.get(FileNameField.NAME).toString());

        switch (columnIndex) {
            case 0:
                ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
                if (icon != null) {
                    icon.setDescription(file.getName());
                }
                ret = icon;
                break;
            case 1:
                ret = FileUtil.humanReadableByteCount(doc.getField(SizeField.NAME).numericValue().longValue(), true);
                break;
            case 2:
                ret = DateUtil.formatDate(new Date(doc.getField(LastModifiedField.NAME).numericValue().longValue()));
                break;
            case 3:
                ret = FileSystemView.getFileSystemView().getSystemTypeDescription(file);
                break;
            case 4:
                ret = file.getAbsolutePath();
                break;
        }
        return ret;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
        String ret = "";
        switch (column) {
            case 0:
                ret = "Name";
                break;
            case 1:
                ret = "Size";
                break;
            case 2:
                ret = "Date modified";
                break;
            case 3:
                ret = "Type";
                break;
            case 4:
                ret = "Path";
                break;
        }
        return ret;
    }
}
