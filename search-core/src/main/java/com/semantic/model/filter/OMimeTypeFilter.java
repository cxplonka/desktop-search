/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.MimeTypeField;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OMimeTypeFilter extends OntologyNode implements IQueryGenerator {

    public static final String PROPERTY_MIMETYPE = "property_mimetype";
    protected Query query;
    @XmlAttribute
    private String mimeType = "unknown";

    public OMimeTypeFilter() {
        this("MimeType");
    }

    public OMimeTypeFilter(String name) {
        super(name);
        this.mimeType = name;
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            query = new TermQuery(new Term(
                    getLuceneField(), getMimeType()));
        }
        return query;
    }

    public void setMimeType(String mimeType) {
        if (!this.mimeType.equals(mimeType)) {
            query = null;
            firePropertyChange(PROPERTY_MIMETYPE, this.mimeType, this.mimeType = mimeType);
        }
    }

    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getLuceneField() {
        return MimeTypeField.NAME;
    }
}
