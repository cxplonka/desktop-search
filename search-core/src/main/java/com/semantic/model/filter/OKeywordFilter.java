/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model.filter;

import com.semantic.lucene.fields.ContentField;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import javax.xml.bind.annotation.XmlAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class OKeywordFilter extends OntologyNode implements IQueryGenerator {

    public static final String PROPERTY_KEYWORD = "property_keyword";
    @XmlAttribute
    private String keyWord = "";
    /* cache query */
    protected Query query;

    public OKeywordFilter() {
        super("Keyword Filter");
    }

    @Override
    public Query createQuery() {
        if (query == null) {
            BooleanQuery.Builder root = new BooleanQuery.Builder();
            String[] terms = keyWord.trim().toLowerCase().split("\\s");
            for (String term : terms) {
                root.add(new TermQuery(new Term(getLuceneField(), term)), BooleanClause.Occur.SHOULD);                
            }
            query = root.build();
        }
        return query;
    }

    @Override
    public String getLuceneField() {
        return ContentField.NAME;
    }

    public void setKeyWord(String keyWord) {
        if (!this.keyWord.equals(keyWord)) {
            query = null;
            firePropertyChange(PROPERTY_KEYWORD, this.keyWord, this.keyWord = keyWord);
        }
    }

    public String getKeyWord() {
        return keyWord;
    }
}