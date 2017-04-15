/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.slideshow;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class CloseDialogAction extends AbstractAction {

    public CloseDialogAction() {
        super("close_dialog");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Window window = SwingUtilities.getWindowAncestor((Component) e.getSource());        
        if (window != null) {            
            window.setVisible(false);
        }
    }
}