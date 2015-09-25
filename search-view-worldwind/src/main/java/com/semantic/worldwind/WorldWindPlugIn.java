/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.worldwind;

import com.semantic.ApplicationContext;
import com.semantic.plugin.Context;
import com.semantic.plugin.IPlugIn;
import com.semantic.swing.MainFrame;
import com.semantic.util.property.PropertyMap;

/**
 *
 * @author cplonka
 */
public class WorldWindPlugIn extends PropertyMap implements IPlugIn{

    private WorldWindViewAction _viewAction;
    
    @Override
    public void init(Context context) throws Exception {
        /* inject viewaction */
        MainFrame frame = context.get(ApplicationContext.MAIN_VIEW);
        frame.getResultView().addViewAction(_viewAction = new WorldWindViewAction(context));
    }

    @Override
    public void shutdown(Context context) throws Exception {
        _viewAction.dispose();
    }
}