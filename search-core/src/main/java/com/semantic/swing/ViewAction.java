/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.swing;

import java.awt.Component;
import javax.swing.Action;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public interface ViewAction extends Action {
    
    public Component getComponent();
}
