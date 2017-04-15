/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.worldwind;

import com.semantic.ApplicationContext;
import com.semantic.plugin.Context;
import com.semantic.swing.ViewAction;
import com.semantic.util.Disposable;
import com.semantic.worldwind.view.WorldWindView;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class WorldWindViewAction extends AbstractAction implements ViewAction, Disposable {

    private WorldWindView _view;
    private final ApplicationContext _context;

    public WorldWindViewAction(Context context) {
        super("worldwind_2d_view");
        this._context = (ApplicationContext) context;        
        putValue(SHORT_DESCRIPTION, "WorldWind search space.");
        putValue(SMALL_ICON, new ImageIcon(WorldWindViewAction.class.getResource(
                "/resource/worldwind_view.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /* please show me what you have :) */
    }

    @Override
    public Component getComponent() {
        if (_view == null) {
            _view = new WorldWindView();
            _view.setModel(this._context.get(ApplicationContext.MODEL));
        }
        return _view;
    }

    @Override
    public void dispose() {
        if (_view != null) {
            _view.setModel(null);
            _view.getCanvas().shutdown();
            _view.getParent().remove(_view);
        }
    }
}