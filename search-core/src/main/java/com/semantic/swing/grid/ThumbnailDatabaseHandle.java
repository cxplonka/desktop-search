/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.grid;

import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.thumbnail.ThumbnailLoadListener;
import com.semantic.thumbnail.ThumbnailManager;
import com.semantic.util.image.ImageUtil;
import com.semantic.util.lazy.IndexRange;
import com.semantic.util.lazy.LazyList;
import com.semantic.util.lazy.OnLoadEvent;
import com.semantic.util.lazy.OnLoadListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.document.Document;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public abstract class ThumbnailDatabaseHandle implements OnLoadListener,
        ThumbnailLoadListener {

    protected static final Logger log = Logger.getLogger(ThumbnailDatabaseHandle.class.getName());
    /* */
    private final LazyList<Document> lazyList;
    private final ThumbnailManager mThumb;

    public ThumbnailDatabaseHandle(LazyList<Document> lazyList, ThumbnailManager mThumb) {
        this.lazyList = lazyList;
        this.mThumb = mThumb;
        lazyList.addOnLoadListener(this);
        mThumb.addThumbnailLoadListener(this);
    }

    @Override
    public void elementLoaded(OnLoadEvent event) {
        /* try to load the thumbnails in the database */
        IndexRange range = event.getIndexInterval();
        List<File> request = new ArrayList<File>();
        for (int i = range.getStart(); i < range.getEnd(); i++) {
            try {
                Document doc = lazyList.get(i);
                /* check for image document */
                if (ImageUtil.isSupportedExt(doc.get(FileExtField.NAME))) {
                    /* complete filename */
                    String fileName = doc.get(FileNameField.NAME);
                    /* try to get thumbnail image from texture cache */
                    File base = new File(fileName);
                    File thumbFile = new File(mThumb.generateThumbName(base));
                    if (!thumbFile.exists()) {
                        /* add to request */
                        request.add(base);
                    }
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "exception while generating thumbnail.", e);
            }
        }
        /* send an request */
        mThumb.generateThumbnails(request.toArray(new File[request.size()]));
    }
}