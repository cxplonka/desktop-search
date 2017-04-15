/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.akka;

import com.semantic.lucene.handler.LuceneFileHandler;
import java.io.File;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
final class IndexMsg {

    public final LuceneFileHandler.IndexState state;
    public final File file;

    public IndexMsg(LuceneFileHandler.IndexState state, File file) {
        this.state = state;
        this.file = file;
    }
}
