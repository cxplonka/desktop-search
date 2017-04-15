/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing;

import com.semantic.swing.list.DefaultListDocumentRenderer;
import com.semantic.swing.list.MimeTypeListCellRenderer;
import org.apache.lucene.document.Document;
import org.apache.tika.metadata.DublinCore;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class EMailListCellRenderer extends DefaultListDocumentRenderer implements MimeTypeListCellRenderer {

    @Override
    public String getTitle(Document doc) {
        String title = doc.get(DublinCore.TITLE.getName());
        return title == null ? super.getTitle(doc) : title;
    }

    @Override
    public String getType() {
        return "message/rfc822";
    }
}