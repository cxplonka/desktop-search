/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.semantic.eventbus;

/**
 *
 * @author Christian Plonka (cplonka81@gmail.com)
 */
public class GenericEventBus {

    private static GenericEventListenerList listeners;

    public static <T> void addEventListener(Class<T> c, GenericEventListener<T> l) {
        if (listeners == null) {
            listeners = new GenericEventListenerList();
        }
        listeners.add(c, l);
    }

    public static <T> void removeEventListener(Class<T> c, GenericEventListener<T> l) {
        if (listeners != null) {
            listeners.remove(c, l);
        }
    }

    public static <T> void fireEvent(T event) {
        /* maybe push it to an event bus - executor service */
        if (listeners != null) {
            for (GenericEventListener l : listeners.getListeners((Class) event.getClass())) {
                try {
                    l.handleEvent(event);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }
}
