/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.semantic.eventbus;

import java.util.EventListener;

/**
 *
 * @author cplonka
 */
public interface GenericEventListener<T> extends EventListener{

    public void handleEvent(T event);
}
