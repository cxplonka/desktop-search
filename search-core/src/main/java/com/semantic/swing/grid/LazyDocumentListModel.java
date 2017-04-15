/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.grid;

import com.semantic.util.lazy.LazyList;
import com.semantic.util.lazy.OnLoadEvent;
import com.semantic.util.lazy.OnLoadListener;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LazyDocumentListModel extends AbstractListModel implements OnLoadListener {

    private LazyList<Document> lazyList;

    public LazyDocumentListModel() {
    }

    public LazyDocumentListModel(LazyList<Document> lazyList) {
        this.lazyList = lazyList;
    }

    public void setLazyDocuments(LazyList<Document> lazyDocuments) {
        if (this.lazyList != null) {
            this.lazyList.removeOnLoadListener(this);
        }
        this.lazyList = lazyDocuments;
        if (lazyDocuments != null) {
            this.lazyList.addOnLoadListener(this);
        }
    }

    public LazyList<Document> getLazyDocuments() {
        return lazyList;
    }

    protected LazyList<Document> getLazyList() {
        return lazyList;
    }

    @Override
    public int getSize() {
        if (lazyList != null) {
            return lazyList.size();
        }
        return 0;
    }

    @Override
    public Object getElementAt(int arg0) {
        if (getLazyList().isLoaded(arg0)) {
            return getLazyList().get(arg0);
        } else {
            getLazyList().getAsynchronous(arg0);
            return getDummyElementAt(arg0);
        }
    }

    protected Object getDummyElementAt(int row) {
        return "";
    }

    @Override
    public void elementLoaded(final OnLoadEvent event) {
        /* push to edt */
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                /* */
                fireContentChanged();
            }
        });
    }

    public void fireContentChanged() {
        fireContentsChanged(this, 0, 20);
    }
}