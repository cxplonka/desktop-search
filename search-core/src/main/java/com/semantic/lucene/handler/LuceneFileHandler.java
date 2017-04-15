/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene.handler;

import static com.semantic.ApplicationContext.*;
import com.semantic.lucene.fields.FileExtField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.LastModifiedField;
import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.lucene.fields.SizeField;
import com.semantic.lucene.util.IFieldProperty;
import com.semantic.plugin.PlugInManager;
import com.semantic.util.FileUtil;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public abstract class LuceneFileHandler {

    protected static final Logger log = Logger.getLogger(LuceneFileHandler.class.getName());
    /**
     * repository of all registered fields
     */
    protected static final Map<String, IFieldProperty> repository = new HashMap<String, IFieldProperty>();
    /**
     * dummy handle
     */
    private static final IFieldProperty DUMMY = new DummyHandle();

    /**
     * Indexstate of the document
     */
    public static enum IndexState {

        CREATE,
        UPDATE,
        DELETE
    }
    /* */
    public static final MimetypesFileTypeMap MIME_MAP = new MimetypesFileTypeMap();
    public static final String[] IMAGE_MIME_TYPES = {
        "image/bmp bmp",
        "image/png png",
        "image/jpeg jpeg jpg",
        "image/gif gif"
    };
    public static final String[] TEXT_MIME_TYPES = {
        "text/plain txt"
    };

    static {
        for (String type : IMAGE_MIME_TYPES) {
            MIME_MAP.addMimeTypes(type);
        }
        for (String type : TEXT_MIME_TYPES) {
            MIME_MAP.addMimeTypes(type);
        }
        /* load all field definitions */
        PlugInManager mng = instance().get(PLUGIN_MANAGER);
        for (IFieldProperty def : mng.allInstances(IFieldProperty.class)) {
            repository.put(def.getName(), def);
        }
    }

    public static String[] convertMimeTypes(String[] types) {
        String[] ret = new String[types.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = types[i].substring(0, types[i].indexOf(' '));
        }
        return ret;
    }

    public Document handleDocument(IndexWriter indexWriter, IndexState state, File file) throws Exception {
        switch (state) {
            case DELETE:
                indexWriter.deleteDocuments(new Term(FileNameField.NAME, file.getAbsolutePath()));
                return null;
        }
        Document doc = new Document();
        /* file name, when analyzed we're not find it per search query */
        get(FileNameField.NAME).add(doc, file.getAbsolutePath());
        /* file size, watch service problem: 0bytes after add */
        get(SizeField.NAME).add(doc, file.length());
        /* last date of modification */
        get(LastModifiedField.NAME).add(doc, file.lastModified());
        /* file extension */
        get(FileExtField.NAME).add(doc, FileUtil.getFileExtension(file));
        /* try to read mime type, attention detection case sensitive */
        get(MimeTypeField.NAME).add(doc, MIME_MAP.getContentType(
                file.getAbsolutePath().toLowerCase()));
        return doc;
    }

    public IFieldProperty get(String field) {
        IFieldProperty ret = repository.get(field);
        /* dummy handle */
        return ret == null ? DUMMY : ret;
    }

    public abstract String[] getFileExtensions();

    static class DummyHandle implements IFieldProperty<Object> {

        @Override
        public Class getType() {
            return Object.class;
        }

        @Override
        public String getName() {
            return "unknown";
        }

        @Override
        public void add(Document doc, Object value) {
            log.log(Level.FINE, String.format("No field handle found for value [%s]!", value));            
        }
    }
}
