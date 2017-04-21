/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.crawler.filesystem;

import com.semantic.ApplicationContext;
import com.semantic.eventbus.GenericEventBus;
import com.semantic.lucene.IndexManager;
import com.semantic.swing.TrayInfoEvent;
import com.semantic.util.Files;
import com.semantic.util.VisitorPattern;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LuceneIndexWriteTask implements Runnable {

    protected static final Logger log = Logger.getLogger(LuceneIndexWriteTask.class.getName());
    /* */
    private final File[] sources;
    private final IndexManager lucene;

    public LuceneIndexWriteTask(File... sources) {
        this.sources = sources;
        lucene = ApplicationContext.instance().get(IndexManager.LUCENE_MANAGER);
    }

    @Override
    public void run() {
        try {
            for (File source : sources) {
                Files.walkTree(source, new FileVisitor());
                /* finished indexing */
                GenericEventBus.fireEvent(new TrayInfoEvent(
                        "Index", String.format("Finished indexing of directory [%s]", source.getName()),
                        MessageType.INFO));
            }
            /* commit and flush */
            lucene.commit();
            /* */
            log.info(String.format("finished creating index for directories [%s]",
                    Arrays.toString(sources)));
            return;
        } catch (Exception ex) {
            log.log(Level.SEVERE, String.format("exception while index directories [%s]",
                    Arrays.toString(sources)), ex);
        }
        /* not completed indexing */
        GenericEventBus.fireEvent(new TrayInfoEvent(
                "Index", String.format("Error occured while indexing [%s]", Arrays.toString(sources)),
                MessageType.ERROR));
    }

    class FileVisitor implements VisitorPattern<File> {

        @Override
        public boolean visit(File node) {
            if (!node.isDirectory()) {
                lucene.entryCreated(node);
            }
            return true;
        }
    }
}
