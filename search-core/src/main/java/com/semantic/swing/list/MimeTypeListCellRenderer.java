/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.list;

import javax.swing.ListCellRenderer;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface MimeTypeListCellRenderer extends ListCellRenderer<Document> {

    /**
     * 
     * @return mime type mapping
     */
    public String getType();
}