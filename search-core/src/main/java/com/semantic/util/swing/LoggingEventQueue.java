/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.util.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class LoggingEventQueue extends EventQueue {

    private static final Logger log = Logger.getLogger(LoggingEventQueue.class.getName());

    public LoggingEventQueue() {
        super();
    }

    @Override
    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);
        } catch (Throwable t) {
            log.log(Level.SEVERE, "exception on EDT!", t);
        }
    }
}