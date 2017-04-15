/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.tree.querybuilder;

import com.semantic.ApplicationContext;
import static com.semantic.lucene.IndexManager.*;
import com.semantic.lucene.task.LuceneQueryTask;
import com.semantic.model.IQueryGenerator;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class QueryRefreshAction extends AbstractAction implements
        TreeSelectionListener, PropertyChangeListener {
    
    public QueryRefreshAction() {
        super("Find");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ApplicationContext ctx = ApplicationContext.instance();
        /* push to task service */
        ctx.get(LUCENE_MANAGER).getTaskService().submit(
                new LuceneQueryTask(ctx.get(ApplicationContext.QUERY_MANAGER).generateQuery()));
    }
    
    @Override
    public void valueChanged(TreeSelectionEvent tse) {
        actionPerformed(null);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        /* refire the query */
        if (evt.getPropertyName().equals(IQueryGenerator.BOOLEAN_CLAUSE.getName())
                || evt.getPropertyName().equals("property_date")) {
            actionPerformed(null);
        }
    }
}
