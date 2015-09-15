/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.model;

import com.semantic.util.property.IPropertyKey;
import com.semantic.util.property.JAXBKey;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian
 */
public interface IQueryGenerator {

    public static enum CLAUSE {

        AND, OR, NOT
    }
    public static final IPropertyKey<CLAUSE> BOOLEAN_CLAUSE =
            JAXBKey.create("node_boolean_clause", CLAUSE.class, CLAUSE.OR);

    public Query createQuery();

    public String getLuceneField();
}