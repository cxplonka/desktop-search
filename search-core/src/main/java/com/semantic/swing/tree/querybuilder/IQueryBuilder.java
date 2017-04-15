/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.querybuilder;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface IQueryBuilder {

    public Query createQuery();

    public BooleanClause.Occur getCondition();
}
