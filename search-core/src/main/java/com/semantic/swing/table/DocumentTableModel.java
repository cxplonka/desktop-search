/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.table;

import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.lucene.fields.image.ExifDateField;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata.Item;
import org.apache.sanselan.formats.tiff.fieldtypes.FieldTypeASCII;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class DocumentTableModel extends AbstractTableModel {

    private List<String> fields = new ArrayList<String>();
    private List<Object> values = new ArrayList<Object>();

    public void setDocument(Document document) {
        fields.clear();
        values.clear();
        /* analyze fields */
        if (document != null) {
            for (IndexableField field : document.getFields()) {
                fields.add(field.name());
                /* */
                if (field.name().equals(LastModifiedField.NAME)
                        || field.name().equals(ExifDateField.NAME)) {
                    values.add(new Date(field.numericValue().longValue()));
                } else {
                    values.add(field.stringValue() == null
                            ? field.numericValue().toString()
                            : field.stringValue());
                }
            }
            /* list exif */
            try {
                Object mime = document.get(MimeTypeField.NAME);
                if (mime.equals("image/jpeg")) {
                    IImageMetadata metadata = Sanselan.getMetadata(
                            new File(document.get(FileNameField.NAME)));
                    if (metadata != null) {
                        for (Object i : metadata.getItems()) {
                            if (i instanceof TiffImageMetadata.Item) {
                                TiffImageMetadata.Item item = (TiffImageMetadata.Item) i;
                                fields.add(item.getKeyword());
                                values.add(new ModifiableItem(item));
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
        fireTableStructureChanged();
    }

    @Override
    public int getRowCount() {
        return fields.size();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Field Name";
            case 1:
                return "Value Name";
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return fields.get(rowIndex);
            case 1:
                return values.get(rowIndex);
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
                Object ret = values.get(rowIndex);
                if (ret instanceof ModifiableItem) {
                    return ((ModifiableItem) ret).isEditable();
                }
        }
        return super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
                Object ret = values.get(rowIndex);
                if (ret instanceof ModifiableItem) {
                    ((ModifiableItem) ret).setValue(aValue);
                }
        }
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    class ModifiableItem {

        TiffImageMetadata.Item item;

        public ModifiableItem(Item item) {
            this.item = item;
        }

        public boolean isEditable() {
            if (item.getTiffField().fieldType instanceof FieldTypeASCII) {
                return false;
            }
            return false;
        }

        public void setValue(Object object) {
        }

        @Override
        public String toString() {
            try {
                if (isEditable()) {
                    return item.getTiffField().getStringValue().trim();
                }
                return item.getText();
            } catch (ImageReadException ex) {
                return ex.getMessage();
            }
        }
    }
}