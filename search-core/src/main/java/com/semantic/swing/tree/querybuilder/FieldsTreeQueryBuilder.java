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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class FieldsTreeQueryBuilder implements IQueryBuilder {

    private CheckBoxTree tree;

    public FieldsTreeQueryBuilder(CheckBoxTree tree) {
        this.tree = tree;
    }

    @Override
    public Query createQuery() {
        return buildQuery(tree.getCheckBoxTreeSelectionModel());
    }

    @Override
    public BooleanClause.Occur getCondition() {
        return BooleanClause.Occur.MUST;
    }

    private Query buildQuery(CheckBoxTreeSelectionModel smodel) {
        SemanticTreeModel treeModel = (SemanticTreeModel) tree.getModel();
        /* build curQuery, nodes will be call children */
        BooleanQuery.Builder rootQuery = new BooleanQuery.Builder();
        /* evaulate tree */
        MultiMap<String, OntologyNode> fields = new MultiMap<String, OntologyNode>();
        collectFields(treeModel.getRoot(), fields, smodel);
        for (Map.Entry<String, Collection<OntologyNode>> entry : fields.entrySet()) {
            /* collect query's */
            BooleanQuery.Builder subQuery = new BooleanQuery.Builder();
            for (OntologyNode queryNode : entry.getValue()) {
                IQueryGenerator query = (IQueryGenerator) queryNode;
                switch (queryNode.get(IQueryGenerator.BOOLEAN_CLAUSE)) {
                    case AND:
                        subQuery.add(query.createQuery(), BooleanClause.Occur.MUST);
                        break;
                    case NOT:
                        subQuery.add(query.createQuery(), BooleanClause.Occur.MUST_NOT);
                        break;
                    case OR:
                        subQuery.add(query.createQuery(), BooleanClause.Occur.SHOULD);
                        break;
                }
            }
            rootQuery.add(subQuery.build(), BooleanClause.Occur.MUST);
        }
        return rootQuery.build();
    }

    private void collectFields(AbstractOMutableTreeNode node, MultiMap<String, OntologyNode> fields,
            CheckBoxTreeSelectionModel smodel) {
        /* must be selected and an instance from iquerygenerator */
        if (smodel.isPathSelected(new TreePath(node.getPath()), true)
                && node.getUserObject() instanceof IQueryGenerator) {
            IQueryGenerator gen = (IQueryGenerator) node.getUserObject();
            /* collect */
            fields.put(gen.getLuceneField(), node.getUserObject());
        }
        /* look into the childs */
        for (int i = 0; i < node.getChildCount(); i++) {
            collectFields((AbstractOMutableTreeNode) node.getChildAt(i), fields, smodel);
        }
    }
}