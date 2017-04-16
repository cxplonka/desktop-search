/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.table;

import com.semantic.ApplicationContext;
import com.semantic.eventbus.GenericEventBus;
import com.semantic.eventbus.GenericEventListener;
import com.semantic.lucene.task.QueryResultEvent;
import com.semantic.plugin.Context;
import com.semantic.swing.MainFrame;
import com.semantic.swing.ViewAction;
import com.semantic.util.Disposable;
import com.semantic.util.image.TextureManager;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import org.apache.lucene.search.IndexSearcher;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TableViewAction extends AbstractAction implements ViewAction, GenericEventListener<QueryResultEvent>, Disposable {

    private ResultTableView _view;

    private Context context;

    public TableViewAction(Context context) {
        super("table_view");
        this.context = context;
        putValue(SHORT_DESCRIPTION, "View as Table.");
        putValue(SMALL_ICON, new ImageIcon(TextureManager.def().loadImage(
                "table_view.png")));
        this._view = new ResultTableView();

        // hightlight in taxonomy
        MainFrame frame = context.get(ApplicationContext.MAIN_VIEW);
        _view.getResultTable().getSelectionModel().addListSelectionListener(frame.getTreeHighlighter());

        GenericEventBus.addEventListener(QueryResultEvent.class, this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getComponent() {
        return _view;
    }

    @Override
    public void handleEvent(QueryResultEvent event) {
        IndexSearcher currentSearcher = event.getCurrentSearcher();
        /* clear current request states */
        _view.getLazyList().clear();
        /* set new query and searcher */
        _view.getLazyService().setCurrentQuery(currentSearcher, event.getQuery());
        /* update grid view */
        LazyDocumentsTableModel tableModel = (LazyDocumentsTableModel) _view.getResultTable().getModel();
        tableModel.fireTableDataChanged();
    }

    @Override
    public void dispose() {
        GenericEventBus.removeEventListener(QueryResultEvent.class, this);
    }
}
