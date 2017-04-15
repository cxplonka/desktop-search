/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class ExitAction extends AbstractAction{

    public ExitAction() {
        super("Exit");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }    
}