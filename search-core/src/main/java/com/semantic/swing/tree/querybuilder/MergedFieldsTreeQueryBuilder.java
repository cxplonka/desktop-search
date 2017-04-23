/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.querybuilder;

import com.jidesoft.swing.CheckBoxTree;
import com.jidesoft.swing.CheckBoxTreeSelectionModel;
import com.semantic.model.IQueryGenerator;
import com.semantic.model.OntologyNode;
import com.semantic.swing.tree.SemanticTreeModel;
import com.semantic.swing.tree.nodes.AbstractOMutableTreeNode;
import com.semantic.util.MultiMap;
import java.util.Collection;
import java.util.Map;
import javax.swing.tree.TreePath;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class MergedFieldsTreeQueryBuilder implements IQueryBuilder {

    private CheckBoxTree tree;

    public MergedFieldsTreeQueryBuilder(CheckBoxTree tree) {
        this.tree = tree;
    }

    @Override
    public Query createQuery() {
        return buildQuery(tree.getCheckBoxTreeSelectionModel());
    }

    @Override
    public Occur getCondition() {
        return Occur.MUST;
    }

    @Override
    public boolean isBaseQuery() {
        return true;
    }

    private Query buildQuery(CheckBoxTreeSelectionModel smodel) {
        SemanticTreeModel treeModel = (SemanticTreeModel) tree.getModel();
        /* build curQuery, nodes will be call children */
        BooleanQuery.Builder rootQuery = new BooleanQuery.Builder();
        /* evaluate tree */
        MultiMap<String, OntologyNode> andFields = new MultiMap<String, OntologyNode>();
        MultiMap<String, OntologyNode> orFields = new MultiMap<String, OntologyNode>();
        MultiMap<String, OntologyNode> notFields = new MultiMap<String, OntologyNode>();
        /* collect queries by logic and field groups */
        collectFields(treeModel.getRoot(), andFields, smodel, IQueryGenerator.CLAUSE.AND);
        collectFields(treeModel.getRoot(), orFields, smodel, IQueryGenerator.CLAUSE.OR);
        collectFields(treeModel.getRoot(), notFields, smodel, IQueryGenerator.CLAUSE.NOT);
        /* */
        for (Map.Entry<String, Collection<OntologyNode>> entry : andFields.entrySet()) {
            /* collect query's */
            BooleanQuery.Builder subQuery = new BooleanQuery.Builder();
            for (OntologyNode queryNode : entry.getValue()) {
                IQueryGenerator query = (IQueryGenerator) queryNode;
                subQuery.add(query.createQuery(), Occur.SHOULD);
            }
            rootQuery.add(subQuery.build(), Occur.MUST);
        }
        /* */
        for (Map.Entry<String, Collection<OntologyNode>> entry : orFields.entrySet()) {
            /* collect query's */
            BooleanQuery.Builder subQuery = new BooleanQuery.Builder();
            for (OntologyNode queryNode : entry.getValue()) {
                IQueryGenerator query = (IQueryGenerator) queryNode;
                subQuery.add(query.createQuery(), Occur.SHOULD);
            }
            rootQuery.add(subQuery.build(), Occur.MUST);
        }
        /* */
        for (Map.Entry<String, Collection<OntologyNode>> entry : notFields.entrySet()) {
            /* collect query's */
            BooleanQuery.Builder subQuery = new BooleanQuery.Builder();
            for (OntologyNode queryNode : entry.getValue()) {
                IQueryGenerator query = (IQueryGenerator) queryNode;
                subQuery.add(query.createQuery(), Occur.SHOULD);
            }
            rootQuery.add(subQuery.build(), Occur.MUST_NOT);
        }
        return rootQuery.build();
    }

    private void collectFields(AbstractOMutableTreeNode node, MultiMap<String, OntologyNode> fields,
            CheckBoxTreeSelectionModel smodel, IQueryGenerator.CLAUSE clause) {
        /* must be selected and an instance from iquerygenerator */
        if (smodel.isPathSelected(new TreePath(node.getPath()), true)
                && node.getUserObject() instanceof IQueryGenerator
                && node.getUserObject().get(IQueryGenerator.BOOLEAN_CLAUSE).equals(clause)) {
            IQueryGenerator gen = (IQueryGenerator) node.getUserObject();
            /* collect */
            fields.put(gen.getLuceneField(), node.getUserObject());
        }
        /* look into the childs */
        for (int i = 0; i < node.getChildCount(); i++) {
            collectFields((AbstractOMutableTreeNode) node.getChildAt(i), fields, smodel, clause);
        }
    }
}
