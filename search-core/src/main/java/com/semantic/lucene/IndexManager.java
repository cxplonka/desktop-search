/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.lucene;

import com.semantic.ApplicationContext;
import com.semantic.file.event.FileSystemChangeListener;
import com.semantic.lucene.fields.ContentField;
import com.semantic.lucene.fields.FileNameField;
import com.semantic.lucene.fields.image.CommentField;
import com.semantic.lucene.fields.image.ExifMakeField;
import com.semantic.lucene.fields.image.ExifModelField;
import com.semantic.lucene.handler.ImageLuceneFileHandler;
import com.semantic.lucene.handler.LuceneFileHandler;
import com.semantic.lucene.handler.LuceneFileHandler.IndexState;
import com.semantic.lucene.util.IFieldProperty;
import com.semantic.plugin.Context;
import com.semantic.plugin.IPlugIn;
import com.semantic.plugin.PlugInManager;
import com.semantic.swing.MainFrame;
import com.semantic.swing.preferences.GlobalKeys;
import com.semantic.util.FileUtil;
import com.semantic.util.property.IPropertyKey;
import com.semantic.util.property.PropertyKey;
import com.semantic.util.property.PropertyMap;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.SearcherTaxonomyManager;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class IndexManager extends PropertyMap implements FileSystemChangeListener, IPlugIn {

    public static final IPropertyKey<IndexManager> LUCENE_MANAGER
            = PropertyKey.create("LUCENE_MANAGER", IndexManager.class, null);
    /*
     * lucene allow multi threaded search and write to indexsearcher and
     * indexwriter
     */
    private ExecutorService taskService = Executors.newFixedThreadPool(4);
    /*
     * mapping between file extensions and lucene file handles
     */
    private Map<String, LuceneFileHandler> fileHandles;
    /* Specify the analyzer for tokenizing text. The same analyzer should be used for
     * indexing and searching
     */
    private StandardAnalyzer analyzer = new StandardAnalyzer();
    /* create lucene index directory */
    private Directory index;
    /* creates and maintains an index */
    private boolean needsUpdate = false;
    private IndexWriter indexWriter;
    private IndexSearcher indexSearcher;
    /* facet search */
    private Directory taxoIndex;
    private DirectoryTaxonomyWriter taxoWriter;
    private DirectoryTaxonomyReader taxoReader;
    private final FacetsConfig facetConfig = new FacetsConfig();
    private SearcherTaxonomyManager searcherTaxonomyManager;

    public IndexManager() {
        log.setLevel(Level.ALL);
        /* setup indexwriter, same analyzer like for search */
        try {
            index = FSDirectory.open(new File(ApplicationContext.ISEARCH_HOME + "/.lucene").toPath());
            taxoIndex = FSDirectory.open(new File(ApplicationContext.ISEARCH_HOME + "/.lucene/taxonomy").toPath());
            taxoWriter = new DirectoryTaxonomyWriter(taxoIndex, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            /* setup indexwriter */
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setRAMBufferSizeMB(64);
            indexWriter = new IndexWriter(index, config);
            searcherTaxonomyManager = new SearcherTaxonomyManager(indexWriter, true, new SearcherFactory(), taxoWriter);
            log.log(Level.INFO, "lucene indexfilemanager started!");
        } catch (Exception e) {
            log.log(Level.SEVERE, "can not init lucene indexwriter!", e);
        }
    }

    public DirectoryTaxonomyReader getTaxoReader() {
        return taxoReader;
    }

    public FacetsConfig getFacetConfig() {
        return facetConfig;
    }

    public IndexSearcher getIndexSearcher() {
        /* open an NRT reader */
        try {
            if (indexSearcher == null || needsUpdate) {
                /* close old index searcher */
                if (indexSearcher != null) {
                    indexSearcher.getIndexReader().close();
                    taxoReader.close();
                }
                /* commit index */
                indexWriter.commit();
                taxoWriter.commit();
                /* create new indexsearcher on updated index, problems with indexreader.reopen
                 * approach (new index, refresh) */
                indexSearcher = new IndexSearcher(DirectoryReader.open(indexWriter));
                taxoReader = new DirectoryTaxonomyReader(taxoWriter);
                searcherTaxonomyManager = new SearcherTaxonomyManager(indexWriter, true, new SearcherFactory(), taxoWriter);
                needsUpdate = false;
            }
            return indexSearcher;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "can not create index searcher!", ex);
        }
        return null;
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    public DirectoryTaxonomyWriter getTaxoWriter() {
        return taxoWriter;
    }

    public void registerFileHandle(LuceneFileHandler handle) {
        if (fileHandles == null) {
            fileHandles = new HashMap<String, LuceneFileHandler>();
        }
        /* only 1 handle for a extension */
        for (String ext : handle.getFileExtensions()) {
            fileHandles.put(ext, handle);
        }
    }

    public void removeFileHandle(LuceneFileHandler handle) {
        if (fileHandles != null) {
            for (String ext : handle.getFileExtensions()) {
                fileHandles.remove(ext);
            }
        }
    }

    public <T extends LuceneFileHandler> T findHandle(Class<T> clazz) {
        if (fileHandles != null) {
            for (LuceneFileHandler handle : fileHandles.values()) {
                if (handle.getClass().isAssignableFrom(clazz)) {
                    return (T) handle;
                }
            }
        }
        return null;
    }

    public void commit() throws IOException {
        indexWriter.commit();
        taxoWriter.commit();
    }

    public void reset() throws IOException {
        /* delete complete index */
        getIndexWriter().deleteAll();
        getIndexWriter().commit();

        taxoWriter.close();
        taxoIndex.close();

        File taxoDir = new File(ApplicationContext.ISEARCH_HOME + "/.lucene/taxonomy");
        taxoDir.delete();

        taxoIndex = FSDirectory.open(taxoDir.toPath());
        taxoWriter = new DirectoryTaxonomyWriter(taxoIndex, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
    }

    protected void handlePath(IndexState type, File file) {
        /* skip large fiels for the first */
        long mb = file.length() / (1024 * 1024);
        if (file.isDirectory() || mb > 60) {
            return;
        }
        /* */
        LuceneFileHandler fileHandle = fileHandles == null ? null
                : fileHandles.get(FileUtil.getFileExtension(file));
        /* dont handle */
        if (fileHandle == null) {
            /* workaround '*' */
            if (fileHandles != null && fileHandles.containsKey("*")) {
                fileHandle = fileHandles.get("*");
            } else {
                return;
            }
        }
        try {
            switch (type) {
                case CREATE:
                    log.log(Level.FINE, String.format("add path to index [%s]", file));
                    /* create/update index with file */
                    indexWriter.updateDocument(new Term(
                            FileNameField.NAME, file.getAbsolutePath()),
                            handleDocument(fileHandle, IndexState.CREATE, file));
                    break;
                case DELETE:
                    log.log(Level.FINE, String.format("delete path on index [%s]", file));
                    /* handler will delete documents */
                    fileHandle.handleDocument(indexWriter, IndexState.DELETE, file);
                    break;
                case UPDATE:
                    log.log(Level.FINE, String.format("modifiy path on index [%s]", file));
                    /* update the document with the same file name field */
                    indexWriter.updateDocument(new Term(
                            FileNameField.NAME, file.getAbsolutePath()),
                            handleDocument(fileHandle, IndexState.UPDATE, file));
                    break;
            }
            /* need to be commited */
            needsUpdate = true;
            log.info(String.format("successful index: %s - %s - %sbytes",
                    file.getAbsolutePath(), file.exists(), file.length()));
        } catch (Exception e) {
            log.log(Level.WARNING, String.format("can not write file to index! [%s]",
                    file.getAbsolutePath()), e);
        }
    }

    private Document handleDocument(LuceneFileHandler handle, IndexState state, File file) throws Exception {
        Document doc = handle.handleDocument(indexWriter, state, file);
        /* add a all fields field for keyword search */
        StringBuilder buffer = new StringBuilder(file.getAbsolutePath());
        /* camera make model */
        if (doc.getField(ExifMakeField.NAME) != null) {
            buffer.append(' ').append(doc.get(ExifMakeField.NAME));
        }
        /* camera model */
        if (doc.getField(ExifModelField.NAME) != null) {
            buffer.append(' ').append(doc.get(ExifModelField.NAME));
        }
        /* user comment */
        if (doc.getField(CommentField.NAME) != null) {
            buffer.append(' ').append(doc.get(CommentField.NAME));
        }
        /* analyze content */
        doc.add(new TextField(ContentField.NAME, buffer.toString(), Field.Store.NO));
        /* build taxonomy index */
        return facetConfig.build(taxoWriter, doc);
    }

    public SearcherTaxonomyManager getSearcherTaxonomyManager() {
        return searcherTaxonomyManager;
    }

    public ExecutorService getTaskService() {
        return taskService;
    }

    @Override
    public void entryCreated(File file) {
        handlePath(IndexState.CREATE, file);
    }

    @Override
    public void entryDeleted(File file) {
        handlePath(IndexState.DELETE, file);
    }

    @Override
    public void entryModified(File file) {
        handlePath(IndexState.UPDATE, file);
    }

    @Override
    public void init(Context context) throws Exception {
        log.info("init lucene index manager...");
        /* inject use into the context */
        context.set(LUCENE_MANAGER, this);
        /* load and register file handle */
        PlugInManager pluginManager = context.get(ApplicationContext.PLUGIN_MANAGER);
        for (LuceneFileHandler handle : pluginManager.allInstances(LuceneFileHandler.class)) {
            registerFileHandle(handle);
        }
        /* */
        findHandle(ImageLuceneFileHandler.class).setClassifyImage(MainFrame.PREF.getBoolean(
                GlobalKeys.KEY_IMAGE_COLOR_CLASSIFICATION, false));
        /* listen for filesystem events with the lucene manager */
//        context.getProperty(ApplicationContext.FILESYSTEM_MANAGER).
//                addFileSystemChangeListener(this);

        // init facet configuration
        for (IFieldProperty def : pluginManager.allInstances(IFieldProperty.class)) {
            if (def.hasFacet()) {
                facetConfig.setIndexFieldName(def.getName(), def.getName() + "_");
                if (def.isHierachical()) {
                    facetConfig.setHierarchical(def.getName(), true);
                }
            }
        }
    }

    @Override
    public void shutdown(Context context) throws Exception {
        log.info("shutdown lucene index manager...");
        /* remove use from context */
        context.remove(LUCENE_MANAGER);
        index.close();
        taxoIndex.close();
        searcherTaxonomyManager.close();
    }
}
