/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.akka;

import com.semantic.lucene.handler.LuceneFileHandler;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
final class IndexedMsg {

    public final LuceneFileHandler.IndexState state;
    public final Document document;

    public IndexedMsg(LuceneFileHandler.IndexState state, Document document) {
        this.state = state;
        this.document = document;
    }
}