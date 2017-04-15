/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model;

import com.semantic.lucene.fields.FileNameField;
import java.io.File;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ODirectoryNode extends OntologyNode implements IQueryGenerator {

    @XmlAttribute
    private File directory;
    /* cache query */
    protected Query query;

    public ODirectoryNode() {
        super("directory node");
    }

    public ODirectoryNode(File directory) {
        this();
        this.name = directory.getName();
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            String pathQuery = String.format("%s%s", directory.getAbsolutePath(), File.separatorChar);
            query = new PrefixQuery(new Term(getLuceneField(), pathQuery));
        }
        return query;
    }

    @Override
    public String getLuceneField() {
        return FileNameField.NAME;
    }
}