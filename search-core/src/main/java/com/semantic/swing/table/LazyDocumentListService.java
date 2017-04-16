/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.table;

import com.semantic.lucene.task.LuceneQueryTask;
import com.semantic.util.lazy.LazyListService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LazyDocumentListService implements LazyListService<Document> {

    protected static final Logger log = Logger.getLogger(LazyDocumentListService.class.getName());
    /* */
    public static final String FIELD_SESSION_DOCID = "current_session_doc_id";
    private IndexSearcher indexSearcher;
    private Query query;
    private TopDocs topDocs;
    private int pageSize = 20;

    public LazyDocumentListService() {
        this(20);
    }

    public LazyDocumentListService(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setCurrentQuery(IndexSearcher indexSearcher, Query query) {
        this.indexSearcher = indexSearcher;
        this.query = query;
        updateTopDocs(pageSize);
    }

    @Override
    public int getSize() {
        if (topDocs != null) {
            return topDocs.totalHits;
        }
        return 0;
    }

    @Override
    public Document[] getData(int startElement, int endElement) {
        updateTopDocs(endElement);
        /* fill in data, else invalid request */
        if (topDocs != null && (endElement - startElement) > 0) {
            try {
                Document[] ret = new Document[endElement - startElement];
                for (int i = startElement; i < endElement; i++) {
                    ret[i - startElement] = indexSearcher.doc(topDocs.scoreDocs[i].doc);
                    /* inject current docID for this search session */
                    ret[i - startElement].add(new IntPoint(
                            FIELD_SESSION_DOCID,
                            topDocs.scoreDocs[i].doc));
                }
                return ret;
            } catch (Exception ex) {
                log.log(Level.WARNING, "can not fetch documents!", ex);
            }
        }
        return new Document[]{};
    }

    protected void updateTopDocs(int maxDocs) {
        if (indexSearcher != null && maxDocs > 0) {
            try {
                topDocs = indexSearcher.search(query, maxDocs, LuceneQueryTask.SORT);
                log.log(Level.FINE, String.format("updated topDocs cache up to [%s] documents", maxDocs));
            } catch (IOException ex) {
                log.log(Level.WARNING, "can not execute query!", ex);
            }
        }
    }

    @Override
    public void set(int position, Document element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(int position, Document element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(int position) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
