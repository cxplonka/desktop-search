/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.table;

import com.semantic.ApplicationContext;
import com.semantic.plugin.Context;
import com.semantic.plugin.IPlugIn;
import com.semantic.swing.MainFrame;
import com.semantic.util.property.PropertyMap;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class TableViewPlugIn extends PropertyMap implements IPlugIn {

    private TableViewAction _viewAction;

    @Override
    public void init(Context context) throws Exception {
        /* inject viewaction */
        MainFrame frame = context.get(ApplicationContext.MAIN_VIEW);
        frame.getResultView().addViewAction(_viewAction = new TableViewAction(context));
    }

    @Override
    public void shutdown(Context context) throws Exception {
        _viewAction.dispose();
    }
}
